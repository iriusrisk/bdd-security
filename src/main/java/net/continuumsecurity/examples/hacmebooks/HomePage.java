package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/*
 * The page you see after logging in
 */
public class HomePage extends Page {
	static String url = Config.getBaseUrl()+"mainMenu.html";
	
	@FindBy(how = How.ID, using = "keyWords")
	WebElement keyWords;
	
	@FindBy(how = How.CLASS_NAME, using = "loginbutton")
	WebElement searchBtn;

	@FindBy(how = How.LINK_TEXT, using = "Log Out")
	WebElement logout;
	
	
	public HomePage(WebDriver driver) {
		super(url,driver);
	}
	
	@Override
	public HomePage open() {
		return (HomePage)super.open();
	}
	
	public Page logout() {
		logout.click();
		return new Page(driver);
	}
	
	public SearchResultsPage search(String text) {
		keyWords.sendKeys(text);
		searchBtn.click();
		return new SearchResultsPage(driver);
	}
}
