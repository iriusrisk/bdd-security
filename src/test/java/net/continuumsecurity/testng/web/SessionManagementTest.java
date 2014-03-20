package net.continuumsecurity.testng.web;

import net.continuumsecurity.Utils;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;

import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SessionManagementTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    ExamplesTable credentialsTable;

    @BeforeClass
    public void setUp() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.credentialsTable = new ExamplesTable(Utils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/users.table"));
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
    public void the_session_ID_should_be_changed_after_authentication() {
        webAppSteps.openLoginPage();
        webAppSteps.findAndSetSessionIds();
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.compareSessionIds();
    }

    @Test
    public void when_the_user_logs_out_then_the_session_should_no_longer_be_valid() {
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.logout();
        webAppSteps.loginFails();
    }

    @Test
    public void sessions_should_timeout_after_a_period_of_inactivity() {
        webAppSteps.loginFromTable(this.credentialsTable);
    }

    @Test
    public void the_session_cookie_should_have_the_secure_flag_set() {
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.findAndSetSessionIds();
        webAppSteps.sessionCookiesSecureFlag();
    }

    @Test
    public void the_session_cookie_should_have_the_httpOnly_flag_set() {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.sessionCookiesHttpOnlyFlag();
    }

}
