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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;



public class WebApplication {
	public static Logger log;
	Page currentPage;
	protected WebDriver driver;

	public WebApplication(WebDriver driver) {
		log = Logger.getLogger(WebApplication.class);
		log.debug("Constructing new WebApplication");
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	public List<Method> getScannableMethods() {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(SecurityScan.class)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public List<Method> getRestrictedMethods() {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Roles.class)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public void pause(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getAuthorisedRoles(String methodName) {
		try {
			return (List<String>)Arrays.asList(this.getClass().getMethod(methodName, null).getAnnotation(Roles.class).value());
		} catch (SecurityException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.error("The method: "+methodName+" is defined with parameters and has been tagged with the @Roles annotation.  Roles can only be defined on no-argument methods.");
			e.printStackTrace();
		}
		return null;
	}
	
	public void verifyTextPresent(String text) {
		if (!this.driver.getPageSource().contains(text)) throw new UnexpectedContentException("Expected text: ["+text+"] was not found.");
	}
	
}
