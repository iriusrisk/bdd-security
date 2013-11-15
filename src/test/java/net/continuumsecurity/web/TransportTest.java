package net.continuumsecurity.web;

import net.continuumsecurity.Config;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.io.IOException;

public class TransportTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    ExamplesTable credentialsTable;

    //@BeforeClass
    public void setUp() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/users.table"));
    }

    //@AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    //@BeforeTest
    public void beforeScenario() {
        webAppSteps.createAppAndCredentials();
    }

    @Test
    public void testSSLStrength() throws IOException {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.openLoginPage();
        webAppSteps.findResponseWithLoginform();
        webAppSteps.runSSLTestsOnCurrentRequest();
        webAppSteps.sslServiceNotVulnerableToCRIME();
        webAppSteps.sslServiceNotVulnerableToBEAST();
        webAppSteps.sslMinimum128bitCiphers();
        webAppSteps.sslNoV2();
    }

    @Test
    public void setUpDriver() {
        /*ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile myProfile = allProfiles.getProfile("acceptCerts");
        if (myProfile == null) {
            System.out.println("Creatig profile");
            File ffDir = new File(System.getProperty("user.dir")+ File.separator+"ffProfile");
            if (!ffDir.exists()) {
                ffDir.mkdir();
            }
            myProfile = new FirefoxProfile(ffDir);
        }
        myProfile.setAcceptUntrustedCertificates(true);
        myProfile.setAssumeUntrustedCertificateIssuer(true);  */
        DesiredCapabilities cap = new DesiredCapabilities();
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(Config.getProxyHost() + ":" + Config.getProxyPort());
        proxy.setSslProxy(Config.getProxyHost() + ":" + Config.getProxyPort());
        cap.setCapability("proxy", proxy);
        FirefoxDriver driver = new FirefoxDriver(cap);
        driver.get("https://localhost:8443/ropeytasks-0.1/user/login");
    }
}
