/*******************************************************************************
 *    BDD-Security, application security testing framework
 * 
 * Copyright (C) `2014 Stephen de Vries`
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


import net.continuumsecurity.Config;
import net.continuumsecurity.proxy.*;
import org.apache.log4j.Logger;


public class ProxyFactory {
    private static ZAProxyScanner proxy;
	static Logger log = Logger.getLogger(ProxyFactory.class);
	
	public static ScanningProxy getScanningProxy() throws ProxyException {
		if (proxy == null) proxy = new ZAProxyScanner(Config.getProxyHost(),Config.getProxyPort());
		return proxy;
	}

    public static Spider getSpider() throws ProxyException {
        if (proxy == null) proxy = new ZAProxyScanner(Config.getProxyHost(),Config.getProxyPort());
        return proxy;
    }
    
    public static LoggingProxy getLoggingProxy() throws ProxyException {
        if (proxy == null) proxy = new ZAProxyScanner(Config.getProxyHost(),Config.getProxyPort());
        return proxy;
    }
	
	
}
