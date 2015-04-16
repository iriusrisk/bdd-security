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

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import net.continuumsecurity.Config;
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Driver;
import java.util.concurrent.TimeUnit;


public class WebApplication extends Application {
    protected WebDriver driver;
    protected ICaptchaSolver captchaSolver;

    public WebApplication() {
        log = Logger.getLogger(WebApplication.class);
        if (this instanceof ICaptcha) {
        	((ICaptcha)this).setDefaultSolver();
        }
    }

    public ICaptchaSolver getCaptchaSolver() {
        return captchaSolver;
    }

    public void setCaptchaSolver(ICaptchaSolver captchaHelper) {
        this.captchaSolver = captchaHelper;
    }

    public WebDriver getWebDriver() {
        return driver;
    }

    public void setWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void verifyTextPresent(String text) {
        if (!this.driver.getPageSource().contains(text)) throw new UnexpectedContentException("Expected text: ["+text+"] was not found.");
    }

    public void setImplicitWait(long time, TimeUnit unit) {
        DriverFactory.getDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time,unit);
        DriverFactory.getProxyDriver(Config.getInstance().getDefaultDriver()).manage().timeouts().implicitlyWait(time,unit);
    }

    public WebElement findAndWaitForElement(By by) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new NoSuchElementException(e.getMessage());
        }
        return driver.findElement(by);
    }

    public void navigate() {
        driver.get(Config.getInstance().getBaseUrl());
        driver.get(Config.getInstance().getBaseSecureUrl());
    }

    @Override
    public Cookie getCookieByName(String name) {
        return driver.manage().getCookieNamed(name);
    }

    @Override
    public void enableHttpLoggingClient() {
        setWebDriver(DriverFactory.getProxyDriver(Config.getInstance().getDefaultDriver()));
    }

    @Override
    public void enableDefaultClient() {
        setWebDriver(DriverFactory.getDriver(Config.getInstance().getDefaultDriver()));
    }
}
