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
import org.browsermob.core.har.Har;
import org.browsermob.core.har.HarNameValuePair;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import net.continuumsecurity.web.Config;

public class HttpAwareHtmlUnitDriver extends HtmlUnitDriver implements HttpAwareWebDriver {
	Logger log = Logger.getLogger(this.getClass().getName());
	ProxyFacade proxy;
	
	public HttpAwareHtmlUnitDriver() {
		super(true); //enable JavaScript
		log.debug("Constructing EnhancedHtmlUnitDriver");
		getWebClient().setThrowExceptionOnScriptError(false);
		proxy = new ProxyFacade();
		setProxy(proxy.getHost(),proxy.getPort());
	}

	@Override
	public ProxyFacade getProxy() {
		return proxy;
	}
	
	@Override
	public void quit() {
		super.quit();
		proxy.stop();
	}
	
}
