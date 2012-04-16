package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.UnexpectedContentException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class SearchResultsPage extends Page {
	static String url = Config.getBaseUrl()+"searchBooks.html";
	
	@FindBy(how = How.LINK_TEXT, using = "Add to Cart ")
	WebElement firstItem;
	
	public SearchResultsPage(WebDriver driver) {
		super(url,driver);
	}
	
	@Override
	public void verify() {
		super.verify();
		if (getSource().contains("displaying all products")) {
			log.debug("SearchResultsPage verified.");
		} else {
			throw new UnexpectedContentException("Not on search results page");
		}
	}
	
	public Page addFirstItemToCart() {
		firstItem.click();
		return new Page(driver);
	}
	
	
}
