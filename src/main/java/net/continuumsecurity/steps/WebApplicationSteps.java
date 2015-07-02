/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2014 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.steps;

import edu.umass.cs.benchlab.har.HarCookie;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.clients.Browser;
import net.continuumsecurity.proxy.LoggingProxy;
import net.continuumsecurity.proxy.ZAProxyScanner;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.FakeCaptchaSolver;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.WebApplication;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class WebApplicationSteps {
    Logger log = Logger.getLogger(WebApplicationSteps.class);
    public Application app;
    UserPassCredentials credentials;
    HarEntry currentHar;
    LoggingProxy proxy;
    List<Cookie> sessionIds;
    String methodName;
    Map<String, List<HarEntry>> methodProxyMap = new HashMap<String, List<HarEntry>>();
    List<HarEntry> recordedEntries;
    WebElement currentElement;
    private boolean httpHeadersRecorded = false;

    public WebApplicationSteps() {

    }

    @BeforeStories
    public void beforeStories() {
        Config.getInstance().initialiseTables();
    }

    /*
     * This has to be called explicitly when using an examples table in order to
     * start with a fresh browser instance, because @BeforeScenario is only
     * called once for the whole scenario, not each example.
     */
    @Given("a new browser or client instance")
    public void createApp() {
        app = Config.getInstance().createApp();
        app.enableDefaultClient();
        assert app.getClient() != null;
        app.getClient().clearAuthenticationTokens();
        credentials = new UserPassCredentials("", "");
        sessionIds = new ArrayList<Cookie>();
    }

    @Given("the login page")
    @When("the login page is displayed")
    public void openLoginPage() {
        ((ILogin) app).openLoginPage();
    }

    @Given("the default username from: $credentialsTable")
    public void defaultUsername(ExamplesTable credentialsTable) {
        credentials.setUsername(Utils.getDefaultCredentialsFromTable(credentialsTable)
                .getUsername());
        log.debug("username=" + credentials.getUsername());
    }

    @Given("the default password from: $credentialsTable")
    @When("the default password is used from: $credentialsTable")
    public void defaultPassword(ExamplesTable credentialsTable) {
        credentials.setPassword(Utils.getDefaultCredentialsFromTable(credentialsTable)
                .getPassword());
        log.debug("password=" + credentials.getPassword());
    }

    @When("the user logs in")
    @Given("the user logs in")
    public void loginWithSetCredentials() {
        assert credentials != null;
        ((ILogin) app).login(credentials);
    }

    @Given("the default user logs in with credentials from: $credentialsTable")
    @When("the default user logs in with credentials from: $credentialsTable")
    public void loginFromTable(ExamplesTable credentialsTable) {
        assert credentialsTable != null;
        openLoginPage();
        credentials = Utils.getDefaultCredentialsFromTable(credentialsTable);
        loginWithSetCredentials();
    }

    @Given("the username $username")
    public void setUsernameFromExamples(@Named("username") String username) {
        credentials.setUsername(username);
    }

    @Given("an invalid username")
    public void setInvalidUsername() {
        credentials.setUsername(Config.getInstance().getIncorrectUsername());
    }

    @Given("the password $password")
    public void setCredentialsFromExamples(@Named("password") String password) {
        credentials.setPassword(password);
    }

    @Given("an incorrect password")
    public void incorrectPassword() {
        credentials.setPassword(Config.getInstance().getIncorrectPassword());
    }

    @When("the user logs in from a fresh login page")
    public void loginFromFreshPage() {
        createApp();
        openLoginPage();
        loginWithSetCredentials();
    }

    @Then("the user is logged in")
    @Given("the user is logged in")
    @When("the user is logged in")
    public void loginSucceeds() {
        assertThat("The user is logged in", ((ILogin) app).isLoggedIn(), is(true));
    }

    @Then("login fails")
    @Alias("the user is not logged in")
    public void loginFails() {
        assertThat("The user is not logged in", ((ILogin) app).isLoggedIn(), is(false));
    }

    @When("the case of the password is changed")
    public void loginWithWrongCasedPassword() {
        String wrongCasePassword = credentials.getPassword().toUpperCase();

        if (wrongCasePassword.equals(credentials.getPassword())) {
            wrongCasePassword = credentials.getPassword().toLowerCase();
            if (wrongCasePassword.equals(credentials.getPassword())) {
                throw new RuntimeException(
                        "Password doesn't have alphabetic characters, can't run this test.");
            } else {
                credentials.setPassword(wrongCasePassword);
            }
        } else {
            credentials.setPassword(wrongCasePassword);
        }
    }

    @Given("the user logs in from a fresh login page $limit times")
    public void whenTheUserLogsInFromAFreshLoginPageXTimes(int limit) {
        for (int i = 0; i < limit; i++) {
            createApp();
            openLoginPage();
            loginWithSetCredentials();
        }
    }

    @When("the password is changed to values from <value>")
    public void changePasswordTo(@Named("value") String value) {
        credentials.setPassword(value);
    }

    @When("the username is changed to values from <value>")
    public void changeUsernameTo(@Named("value") String value) {
        credentials.setUsername(value);
    }

    @When("an SQL injection <value> is appended to the username")
    public void appendValueToUsername(@Named("value") String value) {
        credentials.setUsername(credentials.getUsername() + value);
    }

    @When("the user logs out")
    @Given("the user logs out")
    public void logout() {
        ((ILogout) app).logout();
    }

    @Given("the browser is configured to use an intercepting proxy")
    public void enableLoggingDriver() {
        app.enableHttpLoggingClient();
    }

    @Given("the proxy logs are cleared")
    @When("the proxy logs are cleared")
    public void clearProxy() {
        getProxy().clear();
    }
    
    public LoggingProxy getProxy() {
    	if (proxy == null) proxy = new ZAProxyScanner(Config.getInstance().getProxyHost(),Config.getInstance().getProxyPort(),Config.getInstance().getProxyApi());
    	return proxy;
    }

    @Given("the HTTP request-response containing the default credentials is selected")
    public void findRequestWithPassword() {
        List<HarEntry> requests = getProxy().findInRequestHistory(credentials.getPassword());
        if (requests == null || requests.size() == 0)
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        currentHar = requests.get(0);
    }


    @Given("the HTTP request containing the string $value is selected")
    public void findRequestWithString(String value) {
        List<HarEntry> requests = getProxy().findInRequestHistory(value);
        if (requests == null || requests.size() == 0)
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        currentHar = requests.get(0);
    }

    @Then("the protocol should be HTTPS")
    public void verifyProtocolHttps() {
        assertThat(currentHar.getRequest().getUrl(), currentHar.getRequest().getUrl().substring(0, 5), equalTo("https"));
    }

    @Given("the HTTP request-response containing the login form")
    public void findResponseWithLoginform() throws UnsupportedEncodingException {
        String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
        List<HarEntry> responses = getProxy().getHistory();
        responses = getProxy().findInResponseHistory(regex);
        if (responses == null || responses.size() == 0)
            throw new StepException(
                    "Could not find HTTP response with password form using regex: "
                            + regex);
        currentHar = responses.get(0);
    }

    @Given("the first HTTP request-response stored by the proxy is selected")
    public void findFirstHar() {
        List<HarEntry> responses = getProxy().getHistory();
        if (responses == null || responses.size() == 0)
            throw new StepException(
                    "No request-responses found");
        currentHar = responses.get(0);
    }

    @Then("the response status code should start with 3")
    public void statusCode3xx() {
        assertThat(Integer.toString(currentHar.getResponse().getStatus()).substring(0, 1), equalTo("3"));
    }

    @Given("the value of the session cookie is noted")
    public void findAndSetSessionIds() {
        for (String name : Config.getInstance().getSessionIDs()) {
            Cookie cookie = ((Browser)app.getClient()).getCookieByName(name);
            if (cookie != null)
                sessionIds.add(cookie);
        }
    }

    @Then("the value of the session cookie issued after authentication should be different from that of the previously noted session ID")
    public void compareSessionIds() {
        Browser browser = (Browser)app.getClient();
        for (String name : Config.getInstance().getSessionIDs()) {
            Cookie initialSessionCookie = findCookieByName(sessionIds, name);
            if (initialSessionCookie != null) {
                String existingCookieValue = findCookieByName(sessionIds, name)
                        .getValue();
                assertThat(browser.getCookieByName(name).getValue(),
                        not(initialSessionCookie.getValue()));
            } else if (browser.getCookieByName(name).getValue() == null) {
                throw new RuntimeException(
                        "No session IDs found after login with name: " + name);
            }
        }
    }

    @Then("the session cookie should have the secure flag set")
    public void sessionCookiesSecureFlag() {
        for (String name : Config.getInstance().getSessionIDs()) {
            assertThat(((Browser)app.getClient()).getCookieByName(name).isSecure(), equalTo(true));
        }
    }

    @Then("the session cookie should have the httpOnly flag set")
    public void sessionCookiesHttpOnlyFlag() {
        int numCookies = Config.getInstance().getSessionIDs().size();
        int cookieCount = 0;
        for (HarEntry entry : getProxy().getHistory()) {
            for (String name : Config.getInstance().getSessionIDs()) {
                for (HarCookie cookie : entry.getResponse().getCookies().getCookies()) {
                    if (cookie.getName().equalsIgnoreCase(name) && cookie.isHttpOnly()) {
                        cookieCount++;
                    }
                }
            }
        }
        assertThat(cookieCount, equalTo(numCookies));
    }

    @When("the session is inactive for $minutes minutes")
    public void waitForTime(int minutes) {
        try {
            Thread.sleep(minutes * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @When("the password field is inspected")
    public void selectPasswordField() {
        checkIfWebApplication();
        String xpath = "//input[@type='password']";
        List<WebElement> passwds = ((WebApplication) app).getBrowser().getWebDriver().findElements(
                By.xpath(xpath));
        if (passwds.size() != 1)
            throw new UnexpectedContentException("Found " + passwds.size() + " password fields using XPath: " + xpath);
        currentElement = passwds.get(0);
    }

    public void checkIfWebApplication() {
        if (!(app instanceof WebApplication)) throw new RuntimeException("This scenario can only be run against a WebApplication");
    }

    @When("the login form is inspected")
    public void selectLoginFormElement() {
        checkIfWebApplication();
        String xpath = "//form[.//input[@type='password']]";
        List<WebElement> loginForms = ((WebApplication) app).getBrowser().getWebDriver().findElements(
                By.xpath(xpath));
        if (loginForms.size() != 1)
            throw new UnexpectedContentException("Found " + loginForms.size() + " login forms using XPath: " + xpath);
        currentElement = loginForms.get(0);
    }

    @Then("it should have the autocomplete attribute set to 'off'")
    public void thenTheLogonFormShouldHaveTheAutocompleteDirectiveSetToOff() {
        assertThat("Autocomplete set to off", currentElement.getAttribute("autocomplete"), equalToIgnoringCase("off"));
    }

    @Then("no exceptions are thrown")
    public void doNothing() {

    }

    @Then("the CAPTCHA should be present")
    public void checkCaptchaRequestPresent() {
        if (!(app instanceof ICaptcha))
            throw new RuntimeException(
                    "Application doesn't implement ICaptcha, can't run captcha scenarios");
        ICaptcha captchaApp = (ICaptcha) app;
        try {
            assertThat(captchaApp.getCaptchaImage(), notNullValue());
        } catch (NoSuchElementException nse) {
            fail("Captcha image not found.");
        }
    }

    @Given("a CAPTCHA solver that always fails")
    public void setIncorrectCaptchaHelper() {
        if (!(app instanceof ICaptcha))
            throw new RuntimeException(
                    "App does not implement ICaptcha, skipping.");
        ((ICaptcha) app).setCaptchaSolver(new FakeCaptchaSolver(app));
    }

    @When("the password recovery feature is requested")
    public void submitPasswordRecovery() {
        ((IRecoverPassword) app).submitRecover(Config.getInstance().getUsers()
                .getAll().get(0).getRecoverPasswordMap());
    }

    @Then("the CAPTCHA is displayed")
    public void checkCaptchaPresent() {
        try {
            assertThat(((ICaptcha) app).getCaptchaImage(), notNullValue());
        } catch (NoSuchElementException nse) {
            fail("CAPTCHA not found");
        }
    }

    @Then("the resource name <method> and HTTP requests should be recorded and stored")
    public void recordFlowInAccessControlMap(@Named("method") String method) {
        if (methodProxyMap.get(method) != null) {
            log.info("The method: "
                    + method
                    + " has already been added to the map, using the existing HTTP logs");
            return;
        }
        methodProxyMap.put(method, getProxy().getHistory());
    }

    @Given("the access control map for authorised users has been populated")
    public void checkIfMapPopulated() {
        if (methodProxyMap.size() == 0)
            throw new RuntimeException(
                    "Access control map has not been populated.");
    }

    @Then("the string: <sensitiveData> should be present in one of the HTTP responses")
    public void checkAccessToResource(
            @Named("sensitiveData") String sensitiveData) {
        try {
            app.getClass().getMethod(methodName).invoke(app);
            // For web services, calling the method might throw an exception if
            // access is denied.
        } catch (Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");
        }
        if (methodProxyMap.get(methodName) != null) {
            log.info("The method: "
                    + methodName
                    + " has already been added to the map, using the existing HTTP logs");
            return;
        }
        methodProxyMap.put(methodName, getProxy().getHistory());
        boolean accessible = getProxy().findInResponseHistory(sensitiveData).size() > 0;
        if (accessible) {
            log.debug("User: " + credentials.getUsername() + " can access resource: " + methodName);
        }
        assertThat("User: " + credentials.getUsername() + " could access resource: " + methodName + " because the text: " + sensitiveData + " was present in the responses", accessible, equalTo(true));
    }

    @When("the response that contains the string: <sensitiveData> is recorded")
    public void recordSensitiveResponse(
            @Named("sensitiveData") String sensitiveData) {
        try {
            app.getClass().getMethod(methodName).invoke(app);
            // For web services, calling the method might throw an exception if
            // access is denied.
        } catch (Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");
        }
        recordedEntries = getProxy().findInResponseHistory(sensitiveData);
        assertThat("The string: " + sensitiveData + " was not found in the HTTP responses", recordedEntries.size(), greaterThan(0));
        currentHar = recordedEntries.get(0);
    }

    @Then("the HTTP Cache-control header should contain the values:")
    public void checkCacheControlHeaders(@Named("value") String value) {
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), Constants.CACHECONTROL), equalTo(value));
    }
    
    @When("they access the restricted resource: <method>")
    @Alias("the previously recorded HTTP Requests for <method> are replayed using the current session ID")
    public void setMethodName(@Named("method") String method) {
    	methodName = method;
    }
        
    
    @When("the HTTP requests and responses on recorded")
    public void noActionForRecordingHttpRequestResponses() {
    	
    }

    @Then("the string: <sensitiveData> should not be present in any of the HTTP responses")
    public void checkNoAccessToResource(@Named("sensitiveData") String sensitiveData) {
        if (methodProxyMap == null || methodProxyMap.get(methodName).size() == 0)
            throw new ConfigurationException(
                    "No HTTP messages were recorded for the method: " + methodName);
        boolean accessible = false;
        findAndSetSessionIds();
        for (HarEntry entry : methodProxyMap.get(methodName)) {
            if (entry.getResponse().getBodySize() > 0) {
                Map<String, String> cookieMap = new HashMap<String, String>();
                for (Cookie cookie : sessionIds) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
                HarRequest manual = null;
                try {
                    manual = Utils.replaceCookies(entry.getRequest(), cookieMap);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    throw new RuntimeException("Could not copy Har request");
                }
                getProxy().clear();
                List<HarEntry> results = getProxy().makeRequest(manual, true);
                results = getProxy().findInResponseHistory(sensitiveData);
                accessible = results != null && results.size() > 0;
                if (accessible) break;
            }
        }
        if (!accessible) {
            log.debug("User: " + credentials.getUsername() + " has no access to resource: " + methodName);
        }
        assertThat(accessible, equalTo(false));
    }

    public Application getWebApplication() {
        return app;
    }

    private Cookie findCookieByName(List<Cookie> cookies, String name) {
        if (cookies.size() == 0)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie == null)
                return null;
            if (cookie.getName().equalsIgnoreCase(name))
                return cookie;
        }
        return null;
    }

    @When("the first HTTP request-response is recorded")
    public void recordFirstHarEntry() {
        List<HarEntry> history = getProxy().getHistory();
        if (history == null || history.size() == 0) throw new RuntimeException("No HTTP requests-responses recorded");
        currentHar = history.get(0);
    }

    @Then("the Strict-Transport-Security header is set")
    public void checkIfHSTSHeaderIsSet() {
        assertThat(Utils.responseContainsHeader(currentHar.getResponse(), Constants.HSTS), equalTo(true));
    }

    @Then("the X-Frame-Options header is either $sameorigin or $deny")
    public void checkIfXFrameOptionsHeaderIsSet(@Named("sameorigin") String sameOrigin, @Named("deny") String deny) {
        assertThat(Utils.responseHeaderValueIsOneOf(currentHar.getResponse(), Constants.XFRAMEOPTIONS, new String[]{sameOrigin, deny}), equalTo(true));
    }

    @Then("the HTTP $name header has the value: $value")
    public void checkHeaderValue(@Named("name") String name, @Named("value") String value) {
        assertNotNull("No HTTP header named: " + name + " was found.", Utils.getResponseHeaderValue(currentHar.getResponse(), name));
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), name), equalTo(value));
    }

    @Then("the Access-Control-Allow-Origin header must not be: $value")
    public void checkThatAccessControlAllowOriginIsNotStar(@Named("value") String star) {
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), Constants.XXSSPROTECTION), not(star));
    }

    @When("the secure base Url is accessed and the HTTP response recorded")
    public void accessSecureBaseUrlAndRecordHTTPResponse() {
        if (!httpHeadersRecorded) {
            enableLoggingDriver();
            clearProxy();
            ((Browser)app.getClient()).getUrl(Config.getInstance().getBaseSecureUrl());
            recordFirstHarEntry();
            httpHeadersRecorded = true;
        }
    }

}
