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
package net.continuumsecurity.web.drivers;

import net.continuumsecurity.Config;
import org.apache.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

public class DriverFactory {
    private final static String CHROME = "chrome";
    private final static String FIREFOX = "firefox";
    private static DriverFactory dm;
    private static WebDriver driver;
    private static WebDriver proxyDriver;
    static Logger log = Logger.getLogger(DriverFactory.class.getName());


    public static DriverFactory instance() {
        if (dm == null)
            dm = new DriverFactory();
        return dm;
    }

    public static WebDriver getProxyDriver(String name) {
        return getDriver(name, true);
    }

    public static WebDriver getDriver(String name) {
        return getDriver(name, false);
    }


    // Return the desired driver and clear all its cookies
    private static WebDriver getDriver(String type, boolean isProxyDriver) {
        WebDriver retVal = instance().findOrCreate(type, isProxyDriver);
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
    private WebDriver findOrCreate(String type, boolean isProxyDriver) {
        if (isProxyDriver) {
            if (proxyDriver != null) return proxyDriver;

            proxyDriver = createProxyDriver(type);
            return proxyDriver;
        } else {
            if (driver != null) return driver;
            driver = createDriver(type);
            return driver;
        }
    }

    private WebDriver createDriver(String type) {
        if (type.equalsIgnoreCase(CHROME)) return createChromeDriver(null);
        else if (type.equalsIgnoreCase(FIREFOX)) return createFirefoxDriver(null);
        throw new RuntimeException("Unknown WebDriver browser: "+type);
    }

    private WebDriver createProxyDriver(String type) {
        if (type.equalsIgnoreCase(CHROME)) return createChromeDriver(createProxyCapabilities());
        else if (type.equalsIgnoreCase(FIREFOX)) return createFirefoxDriver(createProxyCapabilities());
        throw new RuntimeException("Unknown WebDriver browser: "+type);
    }

    public WebDriver createChromeDriver(DesiredCapabilities capabilities) {
        System.setProperty("webdriver.chrome.driver", Config.getDefaultDriverPath());
        if (capabilities != null) {
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            return new ChromeDriver(capabilities);
        } else return new ChromeDriver();

    }

    public WebDriver createFirefoxDriver(DesiredCapabilities capabilities) {
        if (capabilities != null) {
            return new FirefoxDriver(capabilities);
        }

        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile myProfile = allProfiles.getProfile("WebDriver");
        if (myProfile == null) {
            File ffDir = new File(System.getProperty("user.dir")+ File.separator+"ffProfile");
            if (!ffDir.exists()) {
                ffDir.mkdir();
            }
            myProfile = new FirefoxProfile(ffDir);
        }
        myProfile.setAcceptUntrustedCertificates(true);
        myProfile.setAssumeUntrustedCertificateIssuer(true);
        myProfile.setPreference("webdriver.load.strategy", "unstable");
        if (capabilities == null) {
            capabilities = new DesiredCapabilities();
        }
        capabilities.setCapability(FirefoxDriver.PROFILE, myProfile);
        return new FirefoxDriver(capabilities);
    }

    public DesiredCapabilities createProxyCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(Config.getProxyHost() + ":" + Config.getProxyPort());
        proxy.setSslProxy(Config.getProxyHost() + ":" + Config.getProxyPort());
        capabilities.setCapability("proxy", proxy);
        return capabilities;
    }

}
