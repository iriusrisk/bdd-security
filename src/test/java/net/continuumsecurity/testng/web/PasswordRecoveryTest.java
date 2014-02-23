package net.continuumsecurity.testng.web;

import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PasswordRecoveryTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();

    @BeforeClass
    public void setUp() {
        webAppSteps.createApp();
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
    public void verify_that_all_configured_user_accounts_can_login_correctly() {
        webAppSteps.setIncorrectCaptchaHelper();
        webAppSteps.submitPasswordRecovery();
        webAppSteps.checkCaptchaPresent();
    }
}

