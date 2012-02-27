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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.Roles;
import net.continuumsecurity.web.Page;


public class LogoutPage extends Page implements ILogout {
	WebElement img_signout;
	
	static String url = Config.getBaseUrl()+"shop/index.do";
	
	public LogoutPage(WebDriver driver) {
		super(url,driver);
	}
	
	public LogoutPage open() {
		return (LogoutPage)super.open();
	}
	
	public Page logout() {
		img_signout.click();
		return new Page(driver);
	}
	
}
