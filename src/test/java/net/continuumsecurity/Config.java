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
package net.continuumsecurity;

import net.continuumsecurity.scanner.ZapManager;
import net.continuumsecurity.web.Application;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Config {
    private final static Logger log = Logger.getLogger(Config.class.getName());

    protected XMLConfiguration xml;
    private String proxyHost;
    private int proxyPort = 0;
    private String noProxyHosts;
    private String proxyApi;
    private static Config config;

    public Application createApp() {
        Object app = null;
        try {
            Class appClass = Class.forName(Config.getInstance().getClassName());
            app = appClass.newInstance();
            return (Application) app;
        } catch (Exception e) {
            log.warning("Error instantiating the class defined in config.xml");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public synchronized static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    private Config() {
        PropertyConfigurator.configure("log4j.properties");
        loadConfig("config.xml");
    }

    public User getDefaultUser() {
        return new User(getDefaultCredentials());
    }

    public Credentials getDefaultCredentials() {
        return new UserPassCredentials(
                validateAndGetString("defaultUsername"),
                validateAndGetString("defaultPassword"));
    }

    public List<String> getIgnoreUrls() {
        List<String> ignoreUrls = new ArrayList<>();
        for (HierarchicalConfiguration ignoreUrl : getXml().configurationsAt("scanner.ignoreUrl")) {
            ignoreUrls.add(ignoreUrl.getRoot().getValue().toString());
            System.out.println(ignoreUrl);
        }
        return ignoreUrls;
    }

    public List<String> getSpiderUrls() {
        List<String> spiderUrls = new ArrayList<>();
        for (HierarchicalConfiguration ignoreUrl : getXml().configurationsAt("scanner.spiderUrl")) {
            spiderUrls.add(ignoreUrl.getRoot().getValue().toString());
        }
        return spiderUrls;
    }

    public int getMaxDepth() {
        String portAsString = validateAndGetString("scanner.maxDepth");
        if (portAsString != null && portAsString.length() > 0) return Integer.parseInt(portAsString);
        return 10;
    }

    public String getClassName() {
        return validateAndGetString("class");
    }

    public String getBaseUrl() {
        return validateAndGetString("baseUrl");
    }

    public String getDefaultDriver() {
        String driver="htmlunit";
        try {
            driver = validateAndGetString("defaultDriver");
        } catch (RuntimeException e) {
        }
        return driver;
    }

    public String getDefaultDriverPath() {
        String path = null;
        try {
            path = validateAndGetString("defaultDriver[@path]");
            return path;
        } catch (RuntimeException e) {
            log.info("No path to the defaultDriver specified in config.xml, using auto-detection.");
            //Option path not specified
            if (SystemUtils.IS_OS_MAC_OSX) path = "drivers"+File.separator+"chromedriver-mac";
            else if (SystemUtils.IS_OS_WINDOWS) path = "drivers"+File.separator+"chromedriver.exe";
            else if (SystemUtils.IS_OS_LINUX) throw new RuntimeException("Linux detected, please specify the correct chrome driver to use (32 or 64 bit) in the config.xml file");
            else throw new RuntimeException("Could not determine host OS. Specify the correct chrome driver to use for this OS in the config.xml file");
            log.info("Using driver at: "+path);
            return path;
        }
    }

    /*
        If value is defined as a system environment variable, then return its value.
        If not, then search for it in the config file
    */
    private String validateAndGetString(String value) {
        String ret = System.getenv(value);
        if (ret != null) return ret;
        ret = getXml().getString(value);
        if (ret == null) throw new RuntimeException(value+" not defined in config.xml");
        return ret;
    }
    
    private String[] validateAndGetStringArray(String value) {
        String[] ret = getXml().getStringArray(value);
        if (ret == null) throw new RuntimeException(value+" not defined in config.xml");
        return ret;
    }

    public String getSSLyzePath() { return validateAndGetString("sslyze.path"); }
    public String getSSLyzeOption() { return validateAndGetString("sslyze.option"); }

    public String getProxyHost() {
        if (proxyHost != null) return proxyHost;

        try {
            proxyHost = validateAndGetString("proxy.host");
            proxyPort = Integer.parseInt(validateAndGetString("proxy.port"));
            proxyApi = validateAndGetString("proxy.api");
        } catch (RuntimeException e) {
            try {
                proxyPort = ZapManager.getInstance().startZAP(Config.getInstance().getZapPath());
                proxyHost = "127.0.0.1";
                proxyApi = ZapManager.API_KEY;
            } catch (Exception re) {
                log.warning("Error starting embedded ZAP");
                re.printStackTrace();
            }
        }
        return Config.getInstance().proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyApi() {
        return proxyApi;
    }

    public void setProxyApi(String key) {
        proxyApi = key;
    }

    public String getZapPath() {
        return validateAndGetString("zapPath");
    }

    public boolean displayStackTrace() {
        return getXml().getBoolean("displayStackTrace");
    }

    public String getIncorrectUsername() {
        return validateAndGetString("incorrectUsername");
    }

    public String getIncorrectPassword() {
        return validateAndGetString("incorrectPassword");
    }

    public String getLatestReportsDir() {
        return validateAndGetString("latestReportsDir");
    }

    public String getReportsDir() {
        return validateAndGetString("reportsDir");
    }

    public String getNessusUsername() { return validateAndGetString("nessus.username");}

    public String getNessusPassword() { return validateAndGetString("nessus.password");}
    
    public String getNoProxyHosts() { return String.join(",", validateAndGetStringArray("upstreamProxy.noProxyHosts"));}

    public String getUpstreamProxyHost() { return validateAndGetString("upstreamProxy.host"); }

    public int getUpstreamProxyPort() {
        String portAsString = validateAndGetString("upstreamProxy.port");
        if (portAsString != null && portAsString.length() > 0) return Integer.parseInt(portAsString);
        return 80;
    }

    public String getSslHost(){
        return validateAndGetString("sslyze.targetHost");
    }

    public int getSslPort(){
        String portAsString =  validateAndGetString("sslyze.targetPort");
        if (portAsString != null && portAsString.length() > 0) return Integer.parseInt(portAsString);
        return 443;
    }

    public List<String> getSessionIDs() {
        List<String> ids = new ArrayList<String>();
        for (Object o : getXml().getList("sessionIds.name")) {
            ids.add((String) o);
        }
        return ids;
    }

    public void setXml(XMLConfiguration xml) {
        getInstance().xml = xml;
    }

    public String getStoryDir() {
        return System.getProperty("user.dir")
                + File.separator
                + getXml().getString("storyDir");
    }

    public  static XMLConfiguration getXml() {
        return getInstance().xml;
    }

    public void loadConfig(String file) {
        try {
            xml = new XMLConfiguration();
            xml.load(file);
        } catch (ConfigurationException cex) {
            cex.printStackTrace();
        } catch (org.apache.commons.configuration.ConfigurationException e) {
            e.printStackTrace();
        }
    }

}
