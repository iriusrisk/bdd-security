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

import net.continuumsecurity.Restricted;
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.behaviour.ICaptcha;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class WebApplication {
	public static Logger log;
	protected WebDriver driver;
	protected ICaptchaHelper captchaHelper;

	public ICaptchaHelper getCaptchaHelper() {
		return captchaHelper;
	}

	public void setCaptchaHelper(ICaptchaHelper captchaHelper) {
		this.captchaHelper = captchaHelper;
	}

	public WebApplication(WebDriver driver) {
		log = Logger.getLogger(WebApplication.class);
		log.debug("Constructing new WebApplication");
		this.driver = driver;
		if (this instanceof ICaptcha) captchaHelper = new CaptchaHelper((ICaptcha)this);
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
			if (method.isAnnotationPresent(Restricted.class)) {
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
			return (List<String>)Arrays.asList(this.getClass().getMethod(methodName, null).getAnnotation(Restricted.class).roles());
		} catch (SecurityException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.error("The method: "+methodName+" is defined with parameters and has been tagged with the @Restricted annotation.  Restricted can only be defined on no-argument methods.");
			e.printStackTrace();
		}
		return null;
	}
	
	public void verifyTextPresent(String text) {
		if (!this.driver.getPageSource().contains(text)) throw new UnexpectedContentException("Expected text: ["+text+"] was not found.");
	}

}
