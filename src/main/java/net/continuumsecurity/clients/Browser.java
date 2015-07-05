package net.continuumsecurity.clients;

import net.continuumsecurity.Config;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephen on 30/06/15.
 */
public class Browser implements SessionClient, SessionTokensInCookies {
    WebDriver driver;

    public Browser(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void clearSessionTokens() {
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


    @Override
    public Map<String, String> getSessionTokens() {
        Map<String,String> tokens = new HashMap<>();
        for (String name : Config.getInstance().getSessionIDs()) {
            Cookie cookie = driver.manage().getCookieNamed(name);
            if (cookie != null)
                tokens.put(cookie.getName(), cookie.getValue());
        }
        return tokens;
    }
}
