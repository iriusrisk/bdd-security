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
package net.continuumsecurity.web.drivers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class DriverFactory {
	private static DriverFactory dm;
	private static List<WebDriver> drivers;
	static Logger log = Logger.getLogger(DriverFactory.class.getName());

	public enum DriverType {
		FIREFOX, HTMLUNIT, BURPFIREFOX, BURPHTMLUNIT
	}

	private DriverFactory() {
		drivers = new ArrayList<WebDriver>();
	}

	public static DriverFactory get() {
		if (dm == null)
			dm = new DriverFactory();
		return dm;
	}

	// Return the desired driver and clear all its cookies
	public static WebDriver getDriver(String type) {
		WebDriver retVal = get().findOrCreate(type);
		try {
			if (!retVal.getCurrentUrl().equals("about:blank")) {
				log.debug("Deleting all cookies on driver: "+type);
				retVal.manage().deleteAllCookies();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			// log.debug("Url: "+retVal.getCurrentUrl()+" Source:\n"+retVal.getPageSource());
		}
		return retVal;
	}

	public static void quitAll() {
		log.debug("closing all drivers");
		for (WebDriver driver : get().drivers) {
			try {
				driver.quit();
			} catch (Exception e) {
				log.error("Error quitting driver: " + e.getMessage());
				e.printStackTrace();
			}
		}
		drivers.clear();
	}

	private WebDriver findOrCreate(String type) {
		if (type.equalsIgnoreCase("burpfirefox"))
			return (findOrCreateClass(BurpFirefoxDriver.class));
		if (type.equalsIgnoreCase("burphtmlunit"))
			return (findOrCreateClass(BurpHtmlUnitDriver.class));
		if (type.equalsIgnoreCase("firefox"))
			return (findOrCreateClass(FirefoxDriver.class));
		if (type.equalsIgnoreCase("htmlunit"))
			return (findOrCreateClass(HtmlUnitDriver.class));
		throw new RuntimeException(
				"Internal error, no suitable WebDriver found.");
	}

	/*
	 * Re-use drivers to reduce startup times
	 */
	private WebDriver findOrCreateClass(Class classType) {
		for (WebDriver driver : drivers) {
			if (classType.isInstance(driver)) {
				
				return driver;
			}
				
		}
		log.debug("No pre-instantiated driver of type: "
				+ classType.getCanonicalName() + " found.\nCreating...");
		try {
			WebDriver driver = (WebDriver) classType.newInstance();
			drivers.add(driver);
			return driver;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Driver could not be instatiated");
	}

}
