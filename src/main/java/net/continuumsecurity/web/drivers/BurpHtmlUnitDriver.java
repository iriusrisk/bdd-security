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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import net.continuumsecurity.Config;

public class BurpHtmlUnitDriver extends HtmlUnitDriver implements BurpDriver {
	private static Logger log;
	
	public BurpHtmlUnitDriver() {
		super();
		log = Logger.getLogger(this.getClass().getName());
		log.debug("Constructing BurpHtmlUnitDriver");
		getWebClient().setThrowExceptionOnScriptError(false);
		setProxy(Config.getBurpHost(),Config.getBurpPort());
	}
	
}
