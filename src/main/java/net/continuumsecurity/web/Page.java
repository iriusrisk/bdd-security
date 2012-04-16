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

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class Page {
	protected static Logger log = Logger.getLogger(Page.class);
	protected String url = "";
	public WebDriver driver;

	public Page() {
	}

	public Page(WebDriver driver) {
		this.driver = driver;
	}

	public Page(String url, WebDriver driver) {
		this.driver = driver;
		this.url = url;
	}

	public Page open() {
		if ("".equals(url))
			throw new UnexpectedContentException(
					"Url has not been set.  Set it through the Page constructor.");
		log.debug(" Getting URL: " + url);
		driver.get(url);
		verify();
		return this;
	}

	/*
	 * Should throw InvalidPageException if the page is not the expected page
	 */
	public void verify() {
		initElements();
		verifyElements();
	}

	public void initElements() {
		PageFactory.initElements(driver, this);
	}

	public String getSource() {
		return driver.getPageSource();
	}

	@Override
	public String toString() {
		return getSource();
	}

	/*
	 * PageFactory.initElements() lazy loads page elements only when they're
	 * actually used. To verify that they exist, we need to iterate through all
	 * declared WebElement fields and perform a dummy operation
	 */
	public void verifyElements() {
		for (Field fld : this.getClass().getDeclaredFields()) {
			if (fld.getType().equals(WebElement.class)) {
				try {
					fld.setAccessible(true);
					WebElement element = (WebElement) fld.get(this);
					element.isDisplayed();
					element.getText();
				} catch (org.openqa.selenium.NoSuchElementException nse) {
					String msg = "Could not find WebElement: " + fld.getName()
							+ " in page: " + this.getClass().getCanonicalName();
					log.error(msg);
					throw new UnexpectedContentException(msg);
				} catch (IllegalStateException ise) {
					String msg = "Could not find WebElement: " + fld.getName()
							+ " in page: " + this.getClass().getCanonicalName();
					log.error(msg);
					throw new UnexpectedContentException(msg);
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
