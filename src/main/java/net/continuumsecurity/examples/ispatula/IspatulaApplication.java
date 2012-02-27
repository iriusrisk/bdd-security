/*******************************************************************************
 *    BDD-Security, application security testing framework
 * 
 * Copyright (C) `2012 Stephen de Vries`
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.examples.ispatula;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Credentials;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.IScanWorkflow;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.Roles;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.WebDriver;

public class IspatulaApplication extends WebApplication implements ILogin,ILogout,IScanWorkflow  {	

	public IspatulaApplication(WebDriver driver) {
		super(driver);
	}
	
	public LoginPage openLoginPage() {
		return new LoginPage(driver).open();
	}
	
	public Page login(Credentials credentials) {
            log.debug(" logging in with credentials: \n"+credentials.toString());
            return openLoginPage().login(credentials);
	}
	
	//Convenience user/pass login method
	public Page login(String username,String password) {
		return login(new UserPassCredentials(username,password));
	}
	
	public Page logout() {
		return new LogoutPage(driver).open().logout();
	}
	
	//Use for automated scanning tests.  navigateAll() is used to direct traffic through Burp, then all URLs are scanned.
	public void navigateAll() {
		search("hello");
		openLoginPage().login(Config.instance().getUsers().getDefaultCredentials("user"));
		new AccountInfoPage(driver).open();
	}
	
	@Roles({"admin"})
	public ListOrdersPage listOrders() {
		return new ListOrdersPage(driver).open();
	}
	
	public Page search(String query) {
		return new SearchPage(driver).open().search(query);
	}
	
	//Check whether the given role is currently logged in.  In it's simplest form, it ignores the role.
	public boolean isLoggedIn(String role) {
		try {
			new AccountInfoPage(driver).open();
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}

}
