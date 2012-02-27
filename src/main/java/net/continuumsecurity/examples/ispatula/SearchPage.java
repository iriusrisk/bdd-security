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
package net.continuumsecurity.examples.ispatula;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Page;


public class SearchPage extends Page {
	
	WebElement query;
	
	@FindBy(how = How.XPATH, using = "//input[@type='image']")
	WebElement submit;
	
	static String url = Config.getBaseUrl()+"shop/Search.do";
	
	public SearchPage(WebDriver driver) {
		super(url,driver);
	}
	
	public SearchPage open() {
		return (SearchPage)super.open();
	}

	public SearchPage search(String term) {
		query.sendKeys(term);
		submit.click();
		return new SearchPage(driver);
	}

	
}
