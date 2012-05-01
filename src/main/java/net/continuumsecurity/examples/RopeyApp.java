package net.continuumsecurity.examples;

import net.continuumsecurity.Credentials;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.web.WebApplication;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA.
 * User: stephen
 * Date: 30/04/2012
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class RopeyApp extends WebApplication implements ILogin, ILogout {

    public RopeyApp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void login(Credentials credentials) {
      /*  UserPassCredentials up = new UserPassCredentials(credentials);
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(up.getUsername());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(up.getPassword());
        driver.findElement(By.name("_action_login")).click();
        */

    }

    @Override
    public void openLoginPage() {
       // driver.get(Config.getBaseUrl() + "user/login");
    }

    @Override
    public boolean isLoggedIn(String role) {
       /* if (driver.getPageSource().contains("Welcome")) return true;
       */ return false;
    }

    @Override
    public void logout() {
      //  driver.findElement(By.linkText("Logout")).click();
    }


    public void userSecScan() {
      /*  openLoginPage();
        login(Config.instance().getUsers().getDefaultCredentials());
        doSearch();
        viewProfile();    */
    }

    public void viewProfile() {
     /*   driver.findElement(By.linkText("Profile")).click();
        verifyTextPresent("Edit User");      */
    }


    public void doSearch() {
      /*  driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys("test");
        driver.findElement(By.xpath("//input[@id='search']")).click();*/
    }
}
