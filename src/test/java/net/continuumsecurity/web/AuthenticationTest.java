package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import net.continuumsecurity.web.NgUtils;
import java.lang.System;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

public class AuthenticationTest {
  protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
  ExamplesTable credentialsTable;
  List<String> sqlInjectionsTable;
  @BeforeClass
  public void setUp() {
    webAppSteps.createApp();
    String workingDirectory = System.getProperty("user.dir"); 
    this.credentialsTable = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory+"/src/main/stories/users.table"));
    this.sqlInjectionsTable = NgUtils.createListOfValues(workingDirectory+"/src/main/stories/tables/sqlinjection.strings.table");
  }

  @Test
  public void password_should_be_case_sensitive(){
    webAppSteps.loginFromTable(this.credentialsTable);
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
  @Test
  public void authentication_credentials_should_be_transmitted_over_SSL() throws UnsupportedEncodingException {
    webAppSteps.setBurpDriver();
    webAppSteps.resetBurp();
    webAppSteps.loginFromTable(this.credentialsTable);
    webAppSteps.findRequestWithPassword();
    webAppSteps.protocolHttps();
  }
  @Test
  public void when_authentication_credentials_are_sent_to_the_server_it_should_respond_with_a_3xx_status_code() {
    webAppSteps.setBurpDriver();
    webAppSteps.resetBurp();
    webAppSteps.loginFromTable(this.credentialsTable);
    webAppSteps.statusCode3xx();
  }
  @Test
  public void The_AUTOCOMPLETE_attribute_should_be_disabled_on_the_password_field() {
    webAppSteps.openLoginPage();
    webAppSteps.thenThePasswordFieldShouldHaveTheAutocompleteDirectiveSetTodisabled();
  }
  @Test
  public void Login_should_be_secure_against_SQL_injection_bypass_attacks_in_the_password_field() {
    for(Object value: this.sqlInjectionsTable) {
      webAppSteps.openLoginPage();
      webAppSteps.defaultUsername(this.credentialsTable);
      webAppSteps.changePasswordTo((String)value);
      webAppSteps.loginWithSetCredentials();
      webAppSteps.loginFails();
    }
  }
}
