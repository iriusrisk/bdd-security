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

import net.continuumsecurity.burpclient.BurpClient;
import net.continuumsecurity.Config;


public class BurpFactory {
	private static BurpClient burp;
	static Logger log = Logger.getLogger(BurpFactory.class);
	
	public static BurpClient getBurp() {
		if (burp == null) burp = new BurpClient(Config.getBurpWSUrl(),Config.getBurpWSProxyHost(),Config.getBurpWSProxyPort());
		return burp;
	}
	
	public static void destroyAll() {
		log.debug("destroying Burp clients");
		getBurp().destroy();
	}
	
	
}
