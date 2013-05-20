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
  protected List<HashMap> = exampleTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir");
    this.exampleTable = new NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/authorised.resources.table");
  }
  
  @BeforeTest
  public void beforeScenario() {
    webAppSteps.createAppAndCredentials();
  }

  @Test
  public void authorised_users_can_view_restricted_resources(){
    for(Object item: this.exampleTable){
      webAppSteps.createApp();
      webAppSteps.setBurpDriver();
      webAppSteps.resetBurp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples(item.get("username"));
      webAppSteps.setPassword(item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.resetBurp();
      webAppSteps.checkAccessToResource(item.get("verifyString"),item.get("method"));
    }
  }

