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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FirefoxDriverFacade implements WebDriver {
	protected Logger log;
	FirefoxDriver ffDriver;

	//Can't be instantiated.  Meant to be used as a convenience base class
	protected FirefoxDriverFacade() {
		log = Logger.getLogger(this.getClass().getName());
		log.debug("Constructing FirefoxDriverFacade");
	}

	@Override
	public void close() {
		ffDriver.close();
	}

	@Override
	public WebElement findElement(By arg0) {
		return ffDriver.findElement(arg0);
	}

	@Override
	public List<WebElement> findElements(By arg0) {
		return ffDriver.findElements(arg0);
	}

	@Override
	public void get(String arg0) {
		ffDriver.get(arg0);
	}

	@Override
	public String getCurrentUrl() {
		return ffDriver.getCurrentUrl();
	}

	@Override
	public String getPageSource() {
		return ffDriver.getPageSource();
	}

	@Override
	public String getTitle() {
		return ffDriver.getTitle();
	}

	@Override
	public String getWindowHandle() {
		return ffDriver.getWindowHandle();
	}

	@Override
	public Set<String> getWindowHandles() {
		return ffDriver.getWindowHandles();
	}

	@Override
	public Options manage() {
		return ffDriver.manage();
	}

	@Override
	public Navigation navigate() {
		return ffDriver.navigate();
	}

	@Override
	public void quit() {
		ffDriver.quit();
	}

	@Override
	public TargetLocator switchTo() {
		return ffDriver.switchTo();
	}

}
