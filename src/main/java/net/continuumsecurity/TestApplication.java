package net.continuumsecurity;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;

import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.INavigable;
import net.continuumsecurity.web.WebApplication;

public class TestApplication extends WebApplication implements INavigable, ILogin, ILogout {
	Logger log = Logger.getLogger(TestApplication.class);

	@Override
	public void openLoginPage() {
		driver.get(Config.getInstance().getLoginUrl());
	}

	@Override
	public void login(Credentials credentials) {
		log.info("Waiting for successful login.");

		UserPassCredentials creds = new UserPassCredentials(credentials);
		String username_field = System.getenv(Constants.SECURITY_USERNAME_FIELD_ID);
		if (username_field != null && username_field.trim().length() > 0) {
			driver.findElement(By.xpath(username_field)).clear();
			driver.findElement(By.xpath(username_field)).sendKeys(creds.getUsername());
		}
		String pass_field = System.getenv(Constants.SECURITY_PASSWORD_FIELD_ID);
		if (pass_field != null && pass_field.trim().length() > 0) {
			driver.findElement(By.xpath(pass_field)).clear();
			driver.findElement(By.xpath(pass_field)).sendKeys(creds.getPassword());
		}
		sleep(5000);
		String submit_button = System.getenv(Constants.SECURITY_SUBMIT_BUTTON_ID);
		if (submit_button != null && submit_button.trim().length() > 0)
			driver.findElement(By.xpath(submit_button)).click();
		log.info("User logged in successfully with user name : " + creds.getUsername());

	}

	@Override
	public boolean isLoggedIn() {
		if (driver.getPageSource().contains("Tasks")) {
			return true;
		} else {
			return false;
		}
	}

	public void navigate() {
		String username = System.getenv(Constants.SECURITY_USERNAME);
		String password = System.getenv(Constants.SECURITY_PASSWORD);
		UserPassCredentials credentials = new UserPassCredentials(username, password);
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			log.info("Navigating to the application without login.");
			driver.get(Config.getInstance().getBaseUrl());
		} else {
			log.info("Navigating to the application login page.");
			openLoginPage();
			log.info("Navigation to the application login page is done.");
			login(credentials);
		}
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
	}

	private void sleep(long sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
