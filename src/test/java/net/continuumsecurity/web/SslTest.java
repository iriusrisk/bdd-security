package net.continuumsecurity.web;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;


public class SslTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @BeforeTest
    public void beforeScenario() throws IOException {
        webAppSteps.createAppAndCredentials();
        webAppSteps.runSSLTestsOnSecureBaseUrl();
    }

    @Test
    public void ssl_not_vulnerable_to_CRIME() throws IOException {
        webAppSteps.sslServiceNotVulnerableToCRIME();
    }

    @Test
    public void ssl_not_vulnerable_to_BEAST() {
        webAppSteps.sslServiceNotVulnerableToBEAST();
    }

    @Test
    public void ssl_minimum_cipher_strength() {
        webAppSteps.sslMinimum128bitCiphers();
    }

    @Test
    public void ssl_version2_disabled() {
        webAppSteps.sslNoV2();
    }

    @Test
    public void ssl_rc4_disabled() {
        webAppSteps.sslNoCipher(Constants.RC4);
    }

}

