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

import org.apache.log4j.Logger;
import org.browsermob.core.har.Har;
import org.browsermob.core.har.HarEntry;
import org.browsermob.core.har.HarNameValuePair;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;

import net.continuumsecurity.web.Config;

public class ProxyFacade {
	ProxyServer server;
	protected Logger log;
	
	public ProxyFacade() {
		log = Logger.getLogger(ProxyFacade.class);
		log.debug("Constructing ProxyFacade");
		int attempt = 0;
		boolean started = false;
		while ((!started) && (attempt < 10)) {
			try {
				server = new ProxyServer(8888+attempt);
				server.start();
				server.newHar(Config.getBaseUrl());
				server.setCaptureHeaders(true);
				server.setCaptureContent(true);
				started = true;
				// server.setCaptureContent(capture);
			} catch (Exception e) {
				attempt++;
			}
		}
		if (!started) {
			throw new RuntimeException("Couldn't find an open port to start the local proxy in the range: 8888 - 8898");
		}
	}
	
	public Proxy getSeleniumProxy() {
		return server.seleniumProxy();
	}

	public void clearLog() {
		server.getHar().getLog().getEntries().clear();
		server.getHar().getLog().getPages().clear();
	}

	public Har getHar() {
		return server.getHar();
	}
	
	public HarNameValuePair findInNameValuePair(List<HarNameValuePair> headers, String regex) {
		for (HarNameValuePair nvp : headers) {
			if (nvp.getName().matches(regex) || nvp.getValue().matches(regex)) return nvp;
		}
		return null;
	}

	
	public String getHost() {
		return getSeleniumProxy().getHttpProxy().split(":")[0];
	}
	
	public int getPort() {
		return Integer.parseInt(getSeleniumProxy().getHttpProxy().split(":")[1]);
	}
	
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
