package net.continuumsecurity.web;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TransportTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    ExamplesTable credentialsTable;
    protected List<HashMap> authorisedTable;
    String msg = "";
    int failures = 0;

    @BeforeClass
    public void setUp() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/users.table"));
        this.authorisedTable = NgUtils.createListOfMaps(workingDirectory + "/src/main/stories/tables/authorised.resources.table");
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @BeforeTest
    public void beforeScenario() {
        webAppSteps.createAppAndCredentials();
    }

    @Test
    public void ssl_service_should_employ_strong_ciphers_and_protocols() throws IOException {
        webAppSteps.runSSLTestsOnSecureBaseUrl();
        webAppSteps.sslServiceNotVulnerableToCRIME();
        webAppSteps.sslServiceNotVulnerableToBEAST();
        webAppSteps.sslMinimum128bitCiphers();
        webAppSteps.sslNoV2();
        webAppSteps.sslNoCipher(Constants.RC4);
    }

    @Test
    public void http_security_headers_should_be_set () {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.openBaseSecureUrl();
        webAppSteps.recordFirstHarEntry();
        webAppSteps.checkIfHSTSHeaderIsSet();
        webAppSteps.checkIfXFrameOptionsHeaderIsSet(Constants.SAMEORIGIN,Constants.DENY);
        webAppSteps.checkIfXSSProtectionHeaderIsSet(Constants.XXSSPROTECTION_VALUE);
        webAppSteps.checkThatAccessControlAllowOriginIsNotStar(Constants.STAR);
        webAppSteps.checkThatXContentTypeHeaderIsNoSniff(Constants.NOSNIFF);
    }

}
