package net.continuumsecurity.testng.web;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.InfrastructureSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;


public class SslTest {
    protected InfrastructureSteps webAppSteps = new InfrastructureSteps();

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @BeforeTest
    public void beforeScenario() throws IOException {
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
        webAppSteps.sslNoV2("2");
    }

    @Test
    public void ssl_rc4_disabled() {
        webAppSteps.sslNoCipher(Constants.RC4);
    }

    @Test
    public void ssl_support_perfect_forward_secrecy() {
        webAppSteps.sslSupportAtLeastOneCipher(Constants.ECDHE_CIPHER);
        webAppSteps.sslSupportAtLeastOneCipher(Constants.DHE_CIPHER);
    }

}

