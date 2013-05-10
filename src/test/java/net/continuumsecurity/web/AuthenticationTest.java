package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import net.continuumsecurity.web.NgUtils;
import java.lang.System;
import java.io.UnsupportedEncodingException;

public class AuthenticationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  ExamplesTable credentialsTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir"); 
    credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory+"/src/main/stories/users.table"));
  }

  @Test
  public void password_should_be_case_sensitive(){
    webAppSteps.loginFromTable(credentialsTable);
    webAppSteps.loginSucceedsVariant2();
    webAppSteps.loginWithWrongCasedPassword();
    webAppSteps.loginFromFreshPage();
    webAppSteps.loginFails();
  }
  @Test
  public void the_login_form_itself_should_be_served_over_SSL() throws UnsupportedEncodingException {
    webAppSteps.setBurpDriver();
    webAppSteps.resetBurp();
    webAppSteps.openLoginPage();
    webAppSteps.findResponseWithLoginform();
    webAppSteps.protocolHttps();
  }

}
