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
package net.continuumsecurity.web;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;


public class PageLoader {
	public static Logger log = Logger.getLogger(PageLoader.class);
    Page page;
    Page loginPage;
	
	WebApplication webApp;
	/*	
	public PageLoader() {
		webApp = new WebApplication();
	}

	
	public static void main (String... args) throws Exception {
    	PageLoader pl = new PageLoader();
    	Class pageClass = Class.forName(args[0]);
    	pl.setPage((Page)pageClass.getDeclaredConstructor(WebDriver.class).newInstance(pl.getWebApp().getDriver()));
    	pl.log.info("Using page: "+pl.getPage().getClass().getName());
    	if ((args.length > 1) && (!"".equals(args[1]))) {
    		pageClass = Class.forName(args[1]);
    		pl.setLoginPage((Page)pageClass.getDeclaredConstructor(WebDriver.class).newInstance(pl.getWebApp().getDriver()));
    		pl.log.info("Using login page: "+pl.getLoginPage().getClass().getName());
    		pl.webApp.open(pl.getLoginPage(), pl.getPage());
    	} else {
    		pl.webApp.open(pl.getPage());
    	}	
    }
    */
    public WebApplication getWebApp() {
		return webApp;
	}

	public void setWebApp(WebApplication webApp) {
		this.webApp = webApp;
	}
	
    public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Page getLoginPage() {
		return loginPage;
	}


	public void setLoginPage(Page loginPage) {
		this.loginPage = loginPage;
	}
}
