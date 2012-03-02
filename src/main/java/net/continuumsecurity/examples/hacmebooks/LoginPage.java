package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class LoginPage extends Page {
	static String url = Config.getBaseUrl()+"mainMenu.html";
	WebElement j_username;
	WebElement j_password;
	
	@FindBy(how = How.XPATH, using = "//input[@name='login' and @value='Login']")
	WebElement submit;
	
	public LoginPage(WebDriver driver) {
		super(url,driver);
	}
	
	public LoginPage open() {
		return (LoginPage)super.open();
	}
	
	public HomePage login(String username,String password) {
		j_username.sendKeys(username);
		j_password.sendKeys(password);
		submit.click();
		return new HomePage(driver);
	}
}
