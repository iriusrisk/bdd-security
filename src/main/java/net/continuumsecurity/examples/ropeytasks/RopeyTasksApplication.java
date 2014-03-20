package net.continuumsecurity.examples.ropeytasks;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.Restricted;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.web.CaptchaSolver;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class RopeyTasksApplication extends WebApplication implements ILogin,
        ILogout, IRecoverPassword, ICaptcha {

    public RopeyTasksApplication() {
        super();
    }

    @Override
    public void openLoginPage() {
        driver.get(Config.getBaseUrl() + "user/login");
        verifyTextPresent("Login");
    }

    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        int attempts = 0;
        boolean captchaPresent = true;
        while (attempts < 4 && captchaPresent) {
			driver.findElement(By.id("username")).clear();
			driver.findElement(By.id("username")).sendKeys(creds.getUsername());
			driver.findElement(By.id("password")).clear();
			driver.findElement(By.id("password")).sendKeys(creds.getPassword());
			if (captchaPresent) {
				captchaSolver.solve();
			}
			driver.findElement(By.name("_action_login")).click();
        	captchaPresent = captchaSolver.isCaptchaPresent();
		}
    }

    // Convenience method
    public void login(String username, String password) {
        login(new UserPassCredentials(username, password));
    }

    @Override
    public boolean isLoggedIn() {
        if (driver.getPageSource().contains("Tasks")) {
            return true;
        } else {
            return false;
        }
    }

    public void viewProfile() {
        driver.findElement(By.linkText("Profile")).click();
    }

    @Restricted(users = {"bob","admin"},
            sensitiveData = "Robert")
    public void viewProfileForBob() {
        driver.get(Config.getBaseUrl() + "user/edit/1");
    }

    @Restricted(users = {"alice","admin"},
            sensitiveData = "alice@continuumsecurity.net")
    public void viewProfileForAlice() {
        driver.get(Config.getBaseUrl() + "user/edit/2");
    }

    @Restricted(users = {"admin"},
            sensitiveData = "User List")
    public void viewUserList() {
        driver.get(Config.getBaseUrl() + "admin/list");
    }

    @Override
    public void logout() {
        driver.findElement(By.linkText("Logout")).click();
    }

    public void search(String query) {
        driver.findElement(By.linkText("Tasks")).click();
        driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys(query);
        driver.findElement(By.id("search")).click();
    }

    public void navigate() {
        openLoginPage();
        login(Config.getUsers().getDefaultCredentials());
        verifyTextPresent("Welcome");
        viewProfile();
        search("test");
    }

    /*
     * The details map will be created from the name and value attributes of the
     * <recoverpassword> tags defined for each user in the config.xml file.
     *
     * (non-Javadoc)
     *
     * @see
     * net.continuumsecurity.behaviour.IRecoverPassword#submitRecover(java.util.Map)
     */
    @Override
    public void submitRecover(Map<String, String> details) {
        driver.get(Config.getBaseUrl() + "user/recover");
        driver.findElement(By.id("email")).sendKeys(details.get("email"));
        driver.findElement(By.xpath("//input[@value='Recover']")).click();
    }
    
    @Override
	public WebElement getCaptchaImage() {
		return driver.findElement(By.id("recaptcha_challenge_image"));		
	}

	@Override
	public WebElement getCaptchaResponseField() {
		return driver.findElement(By.id("recaptcha_response_field"));
	}

	@Override
	public void setDefaultSolver() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("deathbycaptcha.properties"));
			super.setCaptchaSolver(new CaptchaSolver(this, props));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}

