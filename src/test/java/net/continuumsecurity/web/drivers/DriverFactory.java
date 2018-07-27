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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;

public class DriverFactory {
    private final static String CHROME = "chrome";
    private final static String FIREFOX = "firefox";
    private final static String HTMLUNIT = "htmlunit";

    private static DriverFactory dm;
    private static WebDriver driver;
    private static WebDriver proxyDriver;
    static Logger log = Logger.getLogger(DriverFactory.class.getName());


    public static DriverFactory getInstance() {
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
        WebDriver retVal = getInstance().findOrCreate(type, isProxyDriver);
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
        log.debug("closing all webDrivers");
        try {
            if (driver != null) driver.quit();
            if (proxyDriver != null) proxyDriver.quit();
        } catch (Exception e) {
            log.error("Error quitting webDriver: " + e.getMessage());
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
        if (type.equalsIgnoreCase(CHROME)) return createChromeDriver(new DesiredCapabilities());
        else if (type.equalsIgnoreCase(FIREFOX)) return createFirefoxDriver(null);
        else if (type.equalsIgnoreCase(HTMLUNIT)) return createHtmlUnitDriver(null);
        throw new RuntimeException("Unsupported WebDriver browser: "+type);
    }
    private WebDriver createProxyDriver(String type) {
        if (type.equalsIgnoreCase(CHROME)) return createChromeDriver(createProxyCapabilities(CHROME));
        else if (type.equalsIgnoreCase(FIREFOX)) return createFirefoxDriver(createProxyCapabilities(FIREFOX));
	else if (type.equalsIgnoreCase(HTMLUNIT)) return createHtmlUnitDriver(createProxyCapabilities(HTMLUNIT));
	throw new RuntimeException("Unsupported WebDriver browser: "+type);
    }

    public WebDriver createChromeDriver(DesiredCapabilities capabilities) {
        System.setProperty("webdriver.chrome.driver", Config.getInstance().getDefaultDriverPath());
        if (capabilities != null) {
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type");
            capabilities.setCapability(ChromeOptions.CAPABILITY,options);
            return new ChromeDriver(capabilities);
        } else return new ChromeDriver();

    }

    private WebDriver createHtmlUnitDriver(DesiredCapabilities capabilities) {
	if (capabilities != null) {
            capabilities.setBrowserName("htmlunit");
            return new HtmlUnitDriver(capabilities);
        }
        capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("htmlunit");
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return new HtmlUnitDriver(capabilities);
    }

    public WebDriver createFirefoxDriver(DesiredCapabilities capabilities) {

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
	    String noProxyHosts = Config.getInstance().getNoProxyHosts();
	    if (! noProxyHosts.isEmpty()) {
	        myProfile.setPreference("network.proxy.no_proxies_on", noProxyHosts);
	    }
        if (capabilities == null) {
            capabilities = new DesiredCapabilities();
        }
        capabilities.setCapability(FirefoxDriver.PROFILE, myProfile);
        System.setProperty("webdriver.gecko.driver", Config.getInstance().getDefaultDriverPath());
        return new FirefoxDriver(capabilities);
    }

    public DesiredCapabilities createProxyCapabilities(String type) {
        DesiredCapabilities capabilities = null;
	switch (type) {
	case CHROME:
	    capabilities = DesiredCapabilities.chrome();
	    break;
	case FIREFOX:
	    capabilities = DesiredCapabilities.firefox();
	    break;
	case HTMLUNIT:
	    capabilities = DesiredCapabilities.htmlUnit();
	    break;
	default:
	    break;
	}
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(Config.getInstance().getProxyHost() + ":" + Config.getInstance().getProxyPort());
        proxy.setSslProxy(Config.getInstance().getProxyHost() + ":" + Config.getInstance().getProxyPort());
        capabilities.setCapability("proxy", proxy);
        return capabilities;
    }

}
