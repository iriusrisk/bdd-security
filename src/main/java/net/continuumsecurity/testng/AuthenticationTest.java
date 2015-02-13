package net.continuumsecurity.testng;

import java.io.UnsupportedEncodingException;

import net.continuumsecurity.Utils;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;

import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AuthenticationTest extends BrowserBasedTest {


    @BeforeTest
    public void beforeScenario() {
        webAppSteps.createAppAndCredentials();
    }

    @Test
    public void password_should_be_case_sensitive() {
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.loginSucceeds();
        webAppSteps.loginWithWrongCasedPassword();
        webAppSteps.loginFromFreshPage();
        webAppSteps.loginFails();
    }

    @Test
    public void the_login_form_itself_should_be_served_over_SSL() throws UnsupportedEncodingException {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.openLoginPage();
        webAppSteps.findResponseWithLoginform();
        webAppSteps.verifyProtocolHttps();
    }

    @Test
    public void the_login_form_must_not_be_available_over_clear_text_HTTP() throws UnsupportedEncodingException {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.openLoginPage();
        webAppSteps.findResponseWithLoginform();
        webAppSteps.submitLoginOverHttp();
        webAppSteps.verifyProtocolBrowserUrlHttps();
    }

    @Test
    public void authentication_credentials_should_be_transmitted_over_SSL() throws UnsupportedEncodingException {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.findRequestWithPassword();
        webAppSteps.verifyProtocolHttps();
    }

    @Test
    public void when_authentication_credentials_are_sent_to_the_server_it_should_respond_with_a_3xx_status_code() throws UnsupportedEncodingException {
        webAppSteps.enableLoggingDriver();
        webAppSteps.clearProxy();
        webAppSteps.loginFromTable(this.credentialsTable);
        webAppSteps.findRequestWithPassword();
        webAppSteps.statusCode3xx();
    }

    @Test
    public void the_AUTOCOMPLETE_attribute_should_be_disabled_on_the_password_field() {
        webAppSteps.openLoginPage();
        webAppSteps.thenTheLogonFormShouldHaveTheAutocompleteDirectiveSetToOff();
    }

    @Test
    public void the_user_account_should_be_locked_out_after_4_incorrect_authentication_attempts() {
        webAppSteps.defaultUsername(this.credentialsTable);
        webAppSteps.incorrectPassword();
        webAppSteps.whenTheUserLogsInFromAFreshLoginPageXTimes(4);
        webAppSteps.defaultPassword(this.credentialsTable);
        webAppSteps.loginFromFreshPage();
        webAppSteps.loginFails();
    }

    @Test
    public void captcha_should_be_displayed_after_4_incorrect_authentication_attempts() {
        webAppSteps.defaultUsername(this.credentialsTable);
        webAppSteps.incorrectPassword();
        webAppSteps.whenTheUserLogsInFromAFreshLoginPageXTimes(4);
        webAppSteps.openLoginPage();
        webAppSteps.checkCaptchaRequestPresent();
    }
}
