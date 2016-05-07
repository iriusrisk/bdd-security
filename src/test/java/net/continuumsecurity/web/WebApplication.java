/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2014 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.web;

import net.continuumsecurity.Config;
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.clients.AuthTokenManager;
import net.continuumsecurity.clients.Browser;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;


public class WebApplication extends Application {
    protected Browser browser;
    protected WebDriver driver;

    public WebApplication() {
        log = Logger.getLogger(WebApplication.class);
        setImplicitWait(3, TimeUnit.SECONDS);
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
        this.driver = browser.getWebDriver();
    }

    public void verifyTextPresent(String text) {
        if (!this.browser.getWebDriver().getPageSource().contains(text)) throw new UnexpectedContentException("Expected text: ["+text+"] was not found.");
    }

    public void setImplicitWait(long time, TimeUnit unit) {
        DriverFactory.getDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time,unit);
        DriverFactory.getProxyDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time,unit);
    }

    public WebElement findAndWaitForElement(By by) {
        try {
            WebDriverWait wait = new WebDriverWait(browser.getWebDriver(), 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new NoSuchElementException(e.getMessage());
        }
        return browser.getWebDriver().findElement(by);
    }

    public void navigate() {
        browser.getWebDriver().get(Config.getInstance().getBaseUrl());
    }

    @Override
    public void enableHttpLoggingClient() {
        setBrowser(new Browser(DriverFactory.getProxyDriver(Config.getInstance().getDefaultDriver())));
    }

    @Override
    public void enableDefaultClient() {
        setBrowser(new Browser(DriverFactory.getDriver(Config.getInstance().getDefaultDriver())));
    }

    @Override
    public AuthTokenManager getAuthTokenManager() {
        return browser;
    }

}
