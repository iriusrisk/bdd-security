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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Credentials;
import net.continuumsecurity.web.Page;

public class LoginPage extends Page {
	private WebElement username;
	private WebElement password;
	private WebElement update;
	
	static String url = Config.getBaseUrl()+"shop/signonForm.do";
	
	public LoginPage(WebDriver driver) {
		super(url,driver);
	}
	
	public LoginPage open() {
		return (LoginPage)super.open();
	}
	
	public Page login(Credentials credentials) {
		username.sendKeys(credentials.get("username"));
		password.sendKeys(credentials.get("password"));
		update.submit();
		//Return a generic page, because we're not sure whether login succeeded or failed
		return new Page(driver);
	}
	
}
