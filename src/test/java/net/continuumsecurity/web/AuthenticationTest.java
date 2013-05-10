package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import net.continuumsecurity.web.NgUtils;

public class AuthenticationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  ExamplesTable credentialsTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable("../../../../../main/stories/tables/users.tables"));
  }

  @Test
  public void password_should_be_case_sensitive(){
        webAppSteps.loginFromTable(credentialsTable);
  }
}
