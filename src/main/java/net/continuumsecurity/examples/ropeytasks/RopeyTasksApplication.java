package net.continuumsecurity.examples.ropeytasks;

import net.continuumsecurity.web.Captcha;
import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Credentials;
import net.continuumsecurity.web.ICaptcha;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.Roles;
import net.continuumsecurity.web.SecurityScan;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RopeyTasksApplication extends WebApplication implements ILogin,
		ILogout, ICaptcha {
 
	public RopeyTasksApplication(WebDriver driver) {
		super(driver);
	}

	@Override
	public Page login(Credentials credentials) {
		UserPassCredentials creds = new UserPassCredentials(credentials);
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(creds.getUsername());
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(creds.getPassword());
		
		Captcha.instance().solve(this);
		
		driver.findElement(By.name("_action_login")).click();
		return null;
	}
	
	//Convenience method
	public Page login(String username,String password) {
		return login(new UserPassCredentials(username,password));
	}

	@Override
	public Page openLoginPage() {
		driver.get(Config.getBaseUrl() + "user/login");
		verifyTextPresent("Login");
		return null;
	}

	@Override
	public boolean isLoggedIn(String role) {
		if ("admin".equals(role)) {
			driver.get(Config.getBaseUrl() + "admin/list");
			if (driver.getPageSource().contains("Users")) {
				return true;
			} else {
				return false;
			}
		}

		if (driver.getPageSource().contains("Tasks")) {
			return true;
		} else {
			return false;
		}
	}

	
	@SecurityScan
	public void navigateUser() {
		openLoginPage();
		login(Config.instance().getUsers().getDefaultCredentials("user"));
		verifyTextPresent("Welcome");
		viewProfile();
		search("test");
	}
	
	
	public void navigateAdmin() {
		openLoginPage();
		login(Config.instance().getUsers().getDefaultCredentials("admin"));
		verifyTextPresent("Welcome");
		viewUserList();
	}

	/*
	 * If the method is void or returns null, then there must be a check which throws UnexpectedContentException 
	 * if it doesn't complete successfully.  This is the equivalent of the verify() method on the Page class.
	 */
	@Roles({"user"})
	public void viewProfile() {
		driver.findElement(By.linkText("Profile")).click();
		verifyTextPresent("Edit User");
	}
	
	@Roles({"admin"})
	public void viewUserList() {
		driver.get(Config.getBaseUrl() + "admin/list");
		verifyTextPresent("User List");
	}
	
	@Roles({"admin"})
	public void viewUserDetails() {
		driver.get(Config.getBaseUrl() + "admin/show/2");
		verifyTextPresent("Show User");
	}
	
	/*
	 * @Roles annotation can only be used on no-argument methods.
	 */
	@Roles({"user"})
	public void testSearch() {
		search("test");
		verifyTextPresent("Results for");
	}
	
	public void search(String query) {
		driver.findElement(By.linkText("Tasks")).click();
		driver.findElement(By.id("q")).clear();
		driver.findElement(By.id("q")).sendKeys(query);
		driver.findElement(By.xpath("//input[@id='search']")).click();
	}

	@Override
	public Page logout() {
		driver.findElement(By.linkText("Logout")).click();
		return null;
	}

	@Override
	public String getCaptchaImageUrl() {
		//CAPTCHA is only present on the login page, so we assume we're on the login page
		WebElement img = null;
		try {
			img = driver.findElement(By.xpath("//div[@id='recaptcha_image']/img"));
			return img.getAttribute("src");
		} catch (NoSuchElementException nse) {
			log.warn("No captcha image found");
			return null;
		}
	}
	
	@Override
	public WebElement getCaptchaResponseField() {
		return driver.findElement(By.id("recaptcha_response_field")); 
	}


}
