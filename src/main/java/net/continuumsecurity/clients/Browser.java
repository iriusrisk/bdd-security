package net.continuumsecurity.clients;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

/**
 * Created by stephen on 30/06/15.
 */
public class Browser implements GenericClient {
    WebDriver driver;

    public Browser(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void clearAuthenticationTokens() {
        this.driver.manage().deleteAllCookies();
    }

    public Cookie getCookieByName(String name) {
        return this.driver.manage().getCookieNamed(name);
    }

    public WebDriver getWebDriver() {
        return driver;
    }

    public void setWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void getUrl(String url) {
        driver.get(url);
    }
}
