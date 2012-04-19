package net.continuumsecurity.examples.hacmebooks;

import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.Credentials;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.SecurityScan;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.WebDriver;
	
public class HacmeBooksApp extends WebApplication implements ILogin, ILogout {

		public HacmeBooksApp(WebDriver driver) {
			super(driver);
		}

		@SecurityScan
		public void navigateAll() {
			UserPassCredentials creds = new UserPassCredentials(Config.instance().getUsers().getDefaultCredentials());
			HomePage home = (HomePage)openLoginPage().login(creds.getUsername(),creds.getPassword());
			home.verify(); 
			SearchResultsPage results = home.search("hacking");
			results.verify();
			results.addFirstItemToCart();
		}

		@Override
		public Page logout() {
			return new HomePage(driver).open().logout();
		}

		@Override
		public Page login(Credentials credentials) {
			UserPassCredentials creds = new UserPassCredentials(credentials);
			return openLoginPage().login(creds.getUsername(),creds.getPassword());
		}

		@Override
		public LoginPage openLoginPage() {
			return new LoginPage(driver).open();
		}

		@Override
		public boolean isLoggedIn(String role) {
			try {
				new HomePage(driver).open().verify();
				return true;
			} catch (Exception e) {
				return false;
			}
		}

	

}
