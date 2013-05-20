package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import net.continuumsecurity.web.NgUtils;
import java.lang.System;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class AuthorisationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  protected List<HashMap> exampleTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir");
    this.exampleTable = NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/tables/authorised.resources.table");
  }
  
  @BeforeTest
  public void beforeScenario() {
    webAppSteps.createAppAndCredentials();
  }

  @Test
  public void authorised_users_can_view_restricted_resources(){
    for(HashMap item: this.exampleTable){
      webAppSteps.createApp();
      webAppSteps.setBurpDriver();
      webAppSteps.resetBurp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String)item.get("username"));
      webAppSteps.setCredentialsFromExamples((String)item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.resetBurp();
      webAppSteps.checkAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }

  @Test
  public void users_must_not_be_able_to_view_resources_for_which_they_are_not_authorised(){
    for(HashMap item: NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/tables/unauthorised.resources.table")){
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String)item.get("username"));
      webAppSteps.setCredentialsFromExamples((String)item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.checkNoAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }

  @Test
  public void un-authenticated_users_should_not_be_able_to_view_restricted_resources(){
    for(HashMap item: this.exampleTable){
      webAppSteps.checkIfMapPopulated();
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.checkNoAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }
}

