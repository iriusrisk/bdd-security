package net.continuumsecurity.web;

import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class AuthorisationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  protected List<HashMap> authorisedTable;
  protected List<HashMap> unauthorisedTable;

  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir");
    this.authorisedTable = NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/tables/authorised.resources.table");
    this.unauthorisedTable = NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/tables/unauthorised.resources.table");

  }
  
  @BeforeTest
  public void beforeScenario() {
    webAppSteps.createAppAndCredentials();
  }

  @Test
  public void authorised_users_can_view_restricted_resources(){
    for(HashMap item: this.authorisedTable){
      webAppSteps.createApp();
      webAppSteps.enableLoggingDriver();
      webAppSteps.resetProxy();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String) item.get("username"));
      webAppSteps.setCredentialsFromExamples((String) item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.resetProxy();
      webAppSteps.checkAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }

  @Test
  public void users_must_not_be_able_to_view_resources_for_which_they_are_not_authorised(){
    for(HashMap item: this.unauthorisedTable){
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.setUsernameFromExamples((String)item.get("username"));
      webAppSteps.setCredentialsFromExamples((String)item.get("password"));
      webAppSteps.loginWithSetCredentials();
      webAppSteps.checkNoAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }

  @Test
  public void un_authenticated_users_should_not_be_able_to_view_restricted_resources(){
    for(HashMap item: this.authorisedTable){
      webAppSteps.checkIfMapPopulated();
      webAppSteps.createApp();
      webAppSteps.openLoginPage();
      webAppSteps.checkNoAccessToResource((String)item.get("verifyString"),(String)item.get("method"));
    }
  }
}

