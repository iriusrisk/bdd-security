package net.continuumsecurity.examples.ropeytasks;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.Restricted;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.web.WebApplication;

/**
 * Created by stephen on 11/12/14.
 */
public class Demo extends WebApplication implements ILogin, ILogout {
    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(creds.getUsername());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(creds.getPassword());
        driver.findElement(By.id("loginbtn")).click();
    }

    @Override
    public void openLoginPage() {
       driver.get(Config.getBaseUrl()+"user/login");
    }

    @Override
    public boolean isLoggedIn() {
        driver.get(Config.getBaseUrl()+"task/list");
        if (driver.getPageSource().contains("Welcome")) return true;
        return false;
    }

    @Override
    public void logout() {
        driver.get(Config.getBaseUrl()+"user/logout");
    }

    public void navigate() {
        openLoginPage();
        login(Config.getUsers().getDefaultCredentials());
        driver.findElement(By.linkText("Profile")).click();
    }

    @Restricted(users = {"bob"}, sensitiveData = "bob@continuumsecurity.net")
    public void viewBobsProfile() {
        driver.findElement(By.linkText("Profile")).click();
    }
}
