package net.continuumsecurity.web;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

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
    public void http_security_headers_should_be_set () {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.openBaseSecureUrl();
        webAppSteps.recordFirstHarEntry();
        webAppSteps.checkIfHSTSHeaderIsSet();
        webAppSteps.checkIfXFrameOptionsHeaderIsSet(Constants.SAMEORIGIN,Constants.DENY);
        webAppSteps.checkHeaderValue(Constants.XXSSPROTECTION, Constants.XXSSPROTECTION_VALUE);
        webAppSteps.checkThatAccessControlAllowOriginIsNotStar(Constants.STAR);
        webAppSteps.checkHeaderValue(Constants.XCONTENTTYPEOPTIONS, Constants.NOSNIFF);
    }

    @Test
    public void cache_controls_are_set_on_sensitive_content() {
        msg = "";
        failures = 0;
        for (HashMap item : this.authorisedTable) {
            webAppSteps.createApp();
            webAppSteps.enableLoggingDriver();
            webAppSteps.clearProxy();
            webAppSteps.openLoginPage();
            webAppSteps.setUsernameFromExamples((String) item.get("username"));
            webAppSteps.setCredentialsFromExamples((String) item.get("password"));
            webAppSteps.loginWithSetCredentials();
            webAppSteps.clearProxy();
            try {
                webAppSteps.recordSensitiveResponse((String) item.get("sensitiveData"), (String) item.get("method"));
                webAppSteps.checkHeaderValue("Cache-control", "no-cache, no-store, must-revalidate");
                webAppSteps.checkHeaderValue("Pragma", "no-cache");
            } catch (AssertionError e) {
                failures++;
                msg = msg + e.getMessage()+"\n";
            }
        }
        assertEquals(msg, 0, failures);
    }
}
