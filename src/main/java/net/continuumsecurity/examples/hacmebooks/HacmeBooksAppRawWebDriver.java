package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Credentials;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.IScanWorkflow;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.UnexpectedContentException;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HacmeBooksAppRawWebDriver extends WebApplication implements ILogin, ILogout,
		IScanWorkflow {

	public HacmeBooksAppRawWebDriver(WebDriver driver) {
		super(driver);
	}

	@Override
	public void navigateAll() {
		openLoginPage();
		login(Config.instance().getUsers().getDefaultCredentials());
		
		if (!isLoggedIn(null)) throw new UnexpectedContentException("Login failed!");
		
		driver.findElement(By.className("loginbutton")).click();
		driver.findElement(By.id("keyWords")).clear();
		driver.findElement(By.id("keyWords")).sendKeys("hacking");
		driver.findElement(By.className("loginbutton")).click();
		driver.findElement(By.linkText("Add to Cart")).click();
		driver.findElement(By.linkText("View Cart")).click();
	}

	@Override
	public Page logout() {
		driver.findElement(By.linkText("Log Out")).click();
		return null;
	}

	@Override
	public Page login(Credentials credentials) {
		
		UserPassCredentials creds = new UserPassCredentials(credentials);
		
		driver.findElement(By.id("j_username")).clear();
		driver.findElement(By.id("j_username")).sendKeys(creds.getUsername());
		driver.findElement(By.id("j_password")).clear();
		driver.findElement(By.id("j_password")).sendKeys(creds.getPassword());
		driver.findElement(By.xpath("//input[@name='login' and @value='Login']")).click();
		return null;
	}

	@Override
	public Page openLoginPage() {
		driver.get(Config.getBaseUrl()+"mainMenu.html");
		return null;
	}

	@Override
	public boolean isLoggedIn(String role) {
		driver.get(Config.getBaseUrl()+"browseOrders.html");
		if (driver.getPageSource().contains("CreditCardNumber")) {
			return true;
		} else {
			return false;
		}
	}

}
