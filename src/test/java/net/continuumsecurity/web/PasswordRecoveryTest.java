package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;

public class PasswordRecoveryTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
  }
  
  @BeforeTest
  public void beforeScenario() {
    webAppSteps.createAppAndCredentials();
  }

  @Test
  public void verify_that_all_configured_user_accounts_can_login_correctly(){
    webAppSteps.setIncorrectCaptchaHelper();
    webAppSteps.submitPasswordRecovery();
    webAppSteps.checkCaptchaPresent();
  }
}

