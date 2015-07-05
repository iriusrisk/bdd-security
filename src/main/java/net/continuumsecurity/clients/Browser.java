package net.continuumsecurity.clients;

import net.continuumsecurity.Config;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by stephen on 30/06/15.
 */
public class Browser implements AuthTokenManager {
    WebDriver driver;

    public Browser(WebDriver driver) {
        this.driver = driver;
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
    public Map<String, String> getAuthTokens() {
        Map<String,String> tokens = new HashMap<>();
        for (String name : Config.getInstance().getSessionIDs()) {
            Cookie cookie = driver.manage().getCookieNamed(name);
            Set<Cookie> all = driver.manage().getCookies();
            if (cookie != null)
                tokens.put(cookie.getName(), cookie.getValue());
        }
        return tokens;
    }

    @Override
    public void setAuthTokens(Map<String, String> tokens) {
        for (String name : tokens.keySet()) {
            driver.manage().deleteCookieNamed(name);
            driver.manage().addCookie(new Cookie.Builder(name,tokens.get(name)).build());
        }
    }

    @Override
    public void deleteAuthTokens() {
        driver.manage().deleteAllCookies();
    }
}
