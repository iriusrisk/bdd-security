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

import org.apache.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class HttpAwareFirefoxDriver extends FirefoxDriverFacade implements
		HttpAwareWebDriver {
	ProxyFacade proxy;
	
	public HttpAwareFirefoxDriver() {
		log = Logger.getLogger(HttpAwareFirefoxDriver.class);
		log.debug("Constructing EnhancedFirefoxDriver");
		proxy = new ProxyFacade();
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities
				.setCapability(CapabilityType.PROXY, proxy.getSeleniumProxy());

		// start the browser up
		log.debug("starting ffdriver");
		ffDriver = new FirefoxDriver(capabilities);
	}

	@Override
	public ProxyFacade getProxy() {
		return proxy;
	}

	@Override
	public void quit() {
		ffDriver.quit();
		try {
			proxy.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
