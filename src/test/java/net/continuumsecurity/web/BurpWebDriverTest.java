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

import net.continuumsecurity.Config;
import net.continuumsecurity.burpclient.BurpClient;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class BurpWebDriverTest {

	@Test
	public void test() {
		PropertyConfigurator.configure("log4j.properties");
		BurpClient burp = new BurpClient(Config.getBurpWSUrl());
		burp.reset();
		
		//WebDriver driver = DriverFactory.getDriver(DriverType.BURPHTMLUNIT);
		//driver.get(Config.get().baseUrl);
		//DriverFactory.closeAll();
	}

}
