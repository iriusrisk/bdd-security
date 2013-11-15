package net.continuumsecurity.examples.ropeytasks;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.Restricted;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.web.SecurityScan;
import net.continuumsecurity.web.WebApplication;
import org.openqa.selenium.By;

import java.util.Map;

public class RopeyTasksApplication extends WebApplication implements ILogin,
        ILogout, IRecoverPassword {

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
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(creds.getUsername());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(creds.getPassword());
        driver.findElement(By.name("_action_login")).click();
    }

    // Convenience method
    public void login(String username, String password) {
        login(new UserPassCredentials(username, password));
    }

    @Override
    public boolean isLoggedIn(String role) {
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


    @Restricted(roles = { "user" },
            verifyWithText = "Edit User")
    public void viewProfile() {
        driver.findElement(By.linkText("Profile")).click();
    }

    @Restricted(roles = { "admin" },
            verifyWithText = "admin@continuumsecurity.net")
    public void viewUserList() {
        driver.get(Config.getBaseUrl() + "admin/list");
    }

    @Restricted(roles = {"admin"},
            verifyWithText = "admin@continuumsecurity.net")
    public void viewUserDetails() {
        driver.get(Config.getBaseUrl() + "admin/show/3");
    }


    /*
     * @Restricted annotation can only be used on no-argument methods.
     */
    @Restricted(roles = {"user"},
            verifyWithText = "Results for")
    public void testSearch() {
        search("test");
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
        driver.findElement(By.id("email")).sendKeys(details.get("email"));
        driver.findElement(By.xpath("//input[@value='Recover']")).click();
    }


    /* How to login using a captcha
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
     }      */

}

