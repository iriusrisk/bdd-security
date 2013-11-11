/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2012 Stephen de Vries`
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
package net.continuumsecurity.web.drivers;

import net.continuumsecurity.Config;
import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.InvocationTargetException;

public class DriverFactory {
    private final static String CHROME = "chrome";
    private final static String FIREFOX = "firefox";
    private final static String HTMLUNIT = "htmlunit";
    private static DriverFactory dm;
    private static WebDriver driver;
    private static WebDriver proxyDriver;
    static Logger log = Logger.getLogger(DriverFactory.class.getName());


    public static DriverFactory get() {
        if (dm == null)
            dm = new DriverFactory();
        return dm;
    }

    public static WebDriver getProxyDriver(String name) {
        return getDriver(name,true);
    }

    public static WebDriver getDriver(String name) {
        return getDriver(name,false);
    }


    // Return the desired driver and clear all its cookies
    private static WebDriver getDriver(String type,boolean isProxyDriver) {
        WebDriver retVal = get().findOrCreate(type,isProxyDriver);
        try {
            if (!retVal.getCurrentUrl().equals("about:blank")) {
                retVal.manage().deleteAllCookies();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return retVal;
    }




    public static void quitAll() {
        log.debug("closing all drivers");
        try {
            if (driver != null) driver.quit();
            if (proxyDriver != null) proxyDriver.quit();
        } catch (Exception e) {
            log.error("Error quitting driver: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /*
 * Re-use drivers to reduce startup times
 */
    private WebDriver findOrCreate(String type,boolean isProxyDriver) {
        if (isProxyDriver) {
            if (proxyDriver != null) return proxyDriver;

            createProxyDriver(type);
            return proxyDriver;
        } else {
            if (driver != null) return driver;
            createDriver(type);
            return driver;
        }
    }

    public Class getDriverClassFromString(String type) {
        if (type.equalsIgnoreCase(FIREFOX))
            return FirefoxDriver.class;
        if (type.equalsIgnoreCase(HTMLUNIT))
            return HtmlUnitDriver.class;
        if (type.equalsIgnoreCase(CHROME))
            return ChromeDriver.class;
        throw new RuntimeException(
                "Internal error, no suitable WebDriver found for: "+type);
    }

    private WebDriver createDriver(String type) {
        try {
            if (type.equalsIgnoreCase(CHROME)) System.setProperty("webdriver.chrome.driver", Config.getDefaultDriverPath());
            driver = (WebDriver) getDriverClassFromString(type).newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return driver;
    }

    private WebDriver createProxyDriver(String type) {
        try {
            if (type.equalsIgnoreCase(CHROME)) System.setProperty("webdriver.chrome.driver", Config.getDefaultDriverPath());
            proxyDriver = (WebDriver) getDriverClassFromString(type).getDeclaredConstructor(Capabilities.class).newInstance(createProxyCapabilities());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return proxyDriver;
    }

    public DesiredCapabilities createProxyCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(Config.getProxyHost() + ":" + Config.getProxyPort());
        capabilities.setCapability("proxy", proxy);
        return capabilities;
    }

}
