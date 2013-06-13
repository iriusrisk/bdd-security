package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import net.continuumsecurity.web.NgUtils;
import java.lang.System;
import java.util.List;
import java.util.HashMap;

public class ConfigurationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  protected AutomatedScanningSteps automatedScanningSteps = new AutomatedScanningSteps();
  protected List<HashMap> usersTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir");
    this.usersTable = NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/users.table");
    this.automatedScanningSteps.createScanner();
  }
  
  @BeforeTest
  public void beforeScenario() {
    webAppSteps.createAppAndCredentials();
    this.automatedScanningSteps.resetScanner();
  }

  @Test
  public void verify_that_all_configured_user_accounts_can_login_correctly(){
    for(HashMap item: this.usersTable){
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String)item.get("username"));
      webAppSteps.setCredentialsFromExamples((String)item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.loginSucceedsVariant2();
    }
  }
  
  @Test
  public void verify_that_users_are_not_logged_in_when_using_an_incorrect_password(){
    for(HashMap item: this.usersTable){
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String)item.get("username"));
      webAppSteps.incorrectPassword();
      webAppSteps.loginWithSetCredentials();
      webAppSteps.loginFails();
    }
  }
  
  @Test
  public void verify_that_if_users_do_not_login_then_they_are_not_logged_in_According_to_the_ILogin_isLoggedIn_Role_method(){
    webAppSteps.openLoginPage();
    webAppSteps.loginFails();
  }
  
  @Test
  public void verify_that_the_methods_tagged_with_SecurityScan_can_be_navigated_without_errors() throws Exception{
    webAppSteps.openLoginPage();
    automatedScanningSteps.navigateApp();
    webAppSteps.doNothing();
  }
}

