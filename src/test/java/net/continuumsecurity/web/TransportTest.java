package net.continuumsecurity.web;

import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class TransportTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    ExamplesTable credentialsTable;

    @BeforeClass
    public void setUp() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/users.table"));
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

}
