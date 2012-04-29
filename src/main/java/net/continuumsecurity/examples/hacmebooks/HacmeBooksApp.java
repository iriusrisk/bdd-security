package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.web.SecurityScan;
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HacmeBooksApp extends WebApplication implements ILogin, ILogout {

	public HacmeBooksApp(WebDriver driver) {
		super(driver);
	}

	@SecurityScan
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
	public void logout() {
		driver.findElement(By.linkText("Log Out")).click();
	}

	@Override
	public void login(Credentials credentials) {
		
		UserPassCredentials creds = new UserPassCredentials(credentials);
		
		driver.findElement(By.id("j_username")).clear();
		driver.findElement(By.id("j_username")).sendKeys(creds.getUsername());
		driver.findElement(By.id("j_password")).clear();
		driver.findElement(By.id("j_password")).sendKeys(creds.getPassword());
		driver.findElement(By.xpath("//input[@name='login' and @value='Login']")).click();
	}

	@Override
	public void openLoginPage() {
		driver.get(Config.getBaseUrl()+"mainMenu.html");
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
