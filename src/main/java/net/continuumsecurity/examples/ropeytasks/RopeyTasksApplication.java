package net.continuumsecurity.examples.ropeytasks;

import java.util.Map;

import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.web.Roles;
import net.continuumsecurity.web.SecurityScan;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RopeyTasksApplication extends WebApplication implements ILogin,
		ILogout, ICaptcha, IRecoverPassword {

	public RopeyTasksApplication(WebDriver driver) {
		super(driver);
	}

	@Override
	public void login(Credentials credentials) {
		// Captcha solving is not 100% accurate, so try a few times if captcha
		// fails
		boolean captchaPresent = true;
		int attempts = 0;
		while (captchaPresent && attempts < 3) {
			UserPassCredentials creds = new UserPassCredentials(credentials);
			driver.findElement(By.id("username")).clear();
			driver.findElement(By.id("username")).sendKeys(creds.getUsername());
			driver.findElement(By.id("password")).clear();
			driver.findElement(By.id("password")).sendKeys(creds.getPassword());
			captchaHelper.solve();
			driver.findElement(By.name("_action_login")).click();

			captchaPresent = captchaHelper.isPresent();
			attempts++;
		}
	}

	// Convenience method
	public void login(String username, String password) {
		login(new UserPassCredentials(username, password));
	}

	@Override
	public void openLoginPage() {
		driver.get(Config.getBaseUrl() + "user/login");
		verifyTextPresent("Login");
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
	 * If the method is void or returns null, then there must be a check which
	 * throws UnexpectedContentException if it doesn't complete successfully.
	 * This is the equivalent of the verify() method on the Page class.
	 */
	@Roles({ "user" })
	public void viewProfile() {
		driver.findElement(By.linkText("Profile")).click();
		verifyTextPresent("Edit User");
	}

	@Roles({ "admin" })
	public void viewUserList() {
		driver.get(Config.getBaseUrl() + "admin/list");
		verifyTextPresent("User List");
	}

	@Roles({ "admin" })
	public void viewUserDetails() {
		driver.get(Config.getBaseUrl() + "admin/show/2");
		verifyTextPresent("Show User");
	}

	/*
	 * @Roles annotation can only be used on no-argument methods.
	 */
	@Roles({ "user" })
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
	public void logout() {
		driver.findElement(By.linkText("Logout")).click();
	}

	@Override
	public WebElement getCaptchaImage() {
		// CAPTCHA is only present on the login page, so we assume we're on the
		// login page
		return driver.findElement(By.xpath("//div[@id='recaptcha_image']/img"));
	}

	@Override
	public WebElement getCaptchaResponseField() {
		return driver.findElement(By.id("recaptcha_response_field"));
	}

	/*
	 * The details map will be created from the name and value attributes of the
	 * <recoverpassword> tags defined for each user in the config.xml file.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.continuumsecurity.web.IRecoverPassword#submitRecover(java.util.Map)
	 */
	@Override
	public void submitRecover(Map<String, String> details) {
		driver.get(Config.getBaseUrl() + "user/recover");
		int attempts = 0;
		boolean captchaPresent = true;
		while (captchaPresent && attempts < 4) {
			driver.findElement(By.id("email")).sendKeys(details.get("email"));
			captchaHelper.solve();
			driver.findElement(By.xpath("//input[@value='Recover']")).click();
			captchaPresent = captchaHelper.isPresent();
			attempts++;
		}
	}

}
