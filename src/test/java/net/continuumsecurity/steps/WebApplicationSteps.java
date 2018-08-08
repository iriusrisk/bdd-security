package net.continuumsecurity.steps;

/*******************************************************************************
 * BDD-Security, application security testing framework
 * <p>
 * Copyright (C) `2016 Continuum Security`
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/


import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.umass.cs.benchlab.har.HarCookie;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.clients.Browser;
import net.continuumsecurity.proxy.LoggingProxy;
import net.continuumsecurity.proxy.ZAProxyScanner;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.WebApplication;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class WebApplicationSteps {
    Logger log = Logger.getLogger(WebApplicationSteps.class);
    String methodName;
    WebElement currentElement;
    Application app;
    LoggingProxy proxy;

    public WebApplicationSteps() {
    }

    /*
     * This has to be called explicitly when using an examples table in order to
     * start with a fresh browser getInstance, because @BeforeScenario is only
     * called once for the whole scenario, not each example.
     */
    @Given("^a new browser or client instance$")
    public void createAppForAnyClient() {
        createApp();
    }

    public void createApp() {
        app = Config.getInstance().createApp();
        app.enableDefaultClient();
        assert app.getAuthTokenManager() != null;
        app.getAuthTokenManager().deleteAuthTokens();
        World.getInstance().setCredentials(new UserPassCredentials("", ""));
    }

    @When("the authentication tokens on the client are deleted")
    public void deleteAuthTokensOnClient() {
        deleteAuthTokens();
    }

    public void deleteAuthTokens() {
        app.getAuthTokenManager().deleteAuthTokens();
    }

    @Given("^a new browser instance$")
    public void createAppForBrowser() {
        createApp();
        if (!(app.getAuthTokenManager() instanceof Browser)) {
            throw new ConfigurationException("This scenario can only be run with a Browser getInstance, but application.getAuthTokenManager() returns a non-browser client.");
        }
    }

    @When("the login page is displayed")
    public void displayLoginPage() {
        openLoginPage();
    }

    public void openLoginPage() {
        ((ILogin) app).openLoginPage();
    }

    @Given("the login page$")
    public void openLoginPageFromGiven() {
        openLoginPage();
    }

    @When("^the user logs in$")
    public void loginWithSetCredentials() {
        assert World.getInstance().getCredentials() != null;
        ((ILogin) app).login(World.getInstance().getCredentials());
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @When("the default user logs in")
    public void loginDefaultUser() throws IOException {
        openLoginPage();
        setDefaultCredentials(Config.getInstance().getDefaultCredentials());
        loginWithSetCredentials();
    }

    public void setDefaultCredentials(Credentials creds) throws IOException {
        World.getInstance().setCredentials(creds);
    }

    @Given("an invalid username")
    public void setInvalidUsername() {
        World.getInstance().getUserPassCredentials().setUsername(Config.getInstance().getIncorrectUsername());
    }

    @Given("an incorrect password")
    public void incorrectPassword() {
        World.getInstance().getUserPassCredentials().setPassword(Config.getInstance().getIncorrectPassword());
    }

    @When("^the user logs in from a fresh login page$")
    public void loginFromFreshPage() {
        createApp();
        openLoginPage();
        loginWithSetCredentials();
    }

    @Then("the user is logged in")
    public void loginSucceeds() {
        assertThat("The user is logged in", ((ILogin) app).isLoggedIn(), is(true));
    }

    @Then("the user is not logged in")
    public void loginFails() {
        assertThat("The user is not logged in", ((ILogin) app).isLoggedIn(), is(false));
    }

    @When("the case of the password is changed")
    public void changeCaseOfPassword() {
        UserPassCredentials credentials = World.getInstance().getUserPassCredentials();
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

    @Given("^the user logs in from a fresh login page\\ (\\d+) times$")
    public void whenTheUserLogsInFromAFreshLoginPageXTimes(int limit) {
        for (int i = 0; i < limit; i++) {
            createApp();
            openLoginPage();
            loginWithSetCredentials();
        }
    }

    @When("^the user logs out$")
    public void logout() {
        ((ILogout) app).logout();
    }

    @Given("the client/browser is configured to use an intercepting proxy")
    public void enableLoggingDriver() {
        app.enableHttpLoggingClient();
    }

    @When("the proxy logs are cleared")
    public void clearProxy() {
        getProxy().clear();
    }

    public LoggingProxy getProxy() {
        if (proxy == null)
            proxy = new ZAProxyScanner(Config.getInstance().getProxyHost(), Config.getInstance().getProxyPort(), Config.getInstance().getProxyApi());
            proxy.setAttackMode();
        return proxy;
    }

    @Given("the HTTP request-response containing the default credentials is selected")
    public void findRequestWithPassword() {
        UserPassCredentials credentials = World.getInstance().getUserPassCredentials();
        List<HarEntry> all = getProxy().getHistory();
        List<HarEntry> requests = getProxy().findInRequestHistory(credentials.getPassword());
        if (requests == null || requests.size() == 0)
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        World.getInstance().setCurrentHar(requests.get(0));
    }


    @Given("^the HTTP request containing the string (\\s+) is selected$")
    public void findRequestWithString(String value) {
        UserPassCredentials credentials = World.getInstance().getUserPassCredentials();
        List<HarEntry> requests = getProxy().findInRequestHistory(value);
        if (requests == null || requests.size() == 0)
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        World.getInstance().setCurrentHar(requests.get(0));
    }

    @Then("the protocol should be HTTPS")
    public void verifyProtocolHttps() {
        HarEntry currentHar = World.getInstance().getCurrentHar();
        assertThat(currentHar.getRequest().getUrl(), currentHar.getRequest().getUrl().substring(0, 5), equalTo("https"));
    }

    @Given("the HTTP request-response containing the login form")
    public void findResponseWithLoginform() throws UnsupportedEncodingException {
        String regex = "(?i)input[\\s\\w=:'\\-\\\"]*type\\s*=\\s*['\\\"]password['\\\"]";
        List<HarEntry> responses = getProxy().getHistory();
        responses = getProxy().findInResponseHistory(regex);
        if (responses == null || responses.size() == 0)
            throw new StepException(
                    "Could not find HTTP response with password form using regex: "
                            + regex);
        World.getInstance().setCurrentHar(responses.get(0));
    }

    @Given("the first HTTP request-response stored by the proxy is selected")
    public void findFirstHar() {
        List<HarEntry> responses = getProxy().getHistory();
        if (responses == null || responses.size() == 0)
            throw new StepException(
                    "No request-responses found");
        World.getInstance().setCurrentHar(responses.get(0));
    }

    @Then("the response status code should start with 3")
    public void statusCode3xx() {
        assertThat(Integer.toString(World.getInstance().getCurrentHar().getResponse().getStatus()).substring(0, 1), equalTo("3"));
    }

    @Given("the value of the session ID is noted")
    public void findAndSetSessionIds() {
        World.getInstance().getSessionIds().clear();
        World.getInstance().getSessionIds().putAll(app.getAuthTokenManager().getAuthTokens());
    }

    @Then("the value of the session cookie issued after authentication should be different from that of the previously noted session ID")
    public void compareSessionIds() {
        for (String name : World.getInstance().getSessionIds().keySet()) {
            assertThat(app.getAuthTokenManager().getAuthTokens().get(name),
                    not(World.getInstance().getSessionIds().get(name)));
        }
    }

    @Then("the session cookie should have the secure flag set")
    public void sessionCookiesSecureFlag() {
        for (String name : Config.getInstance().getSessionIDs()) {
            assertThat(((Browser) app.getAuthTokenManager()).getCookieByName(name).isSecure(), equalTo(true));
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

    @When("the session is inactive for (\\d+) minutes")
    public void waitForTime(int minutes) {
        try {
            Thread.sleep(minutes * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @When("^the password field is inspected$")
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
        if (!(app instanceof WebApplication))
            throw new RuntimeException("This scenario can only be run against a WebApplication");
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

    @When("they access the restricted resource: (.*)")
    public void setMethodName(String method) {
        methodName = method;
    }

    @When("the response that contains the string: (.*) is recorded")
    public void recordSensitiveResponse(String sensitiveData) {
        UserPassCredentials credentials = World.getInstance().getUserPassCredentials();
        try {
            app.getClass().getMethod(methodName).invoke(app);
            World.getInstance().setRecordedEntries(getProxy().findInResponseHistory(sensitiveData));
            assertThat("The string: " + sensitiveData + " was not found in the HTTP responses", World.getInstance().getRecordedEntries().size(), greaterThan(0));
            World.getInstance().setCurrentHar(World.getInstance().getRecordedEntries().get(0));
        } catch (Exception e) {
            e.printStackTrace();
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");

        }
    }

    @Then("the string: (.*) should be present in one of the HTTP responses")
    public void checkAccessToResource(String sensitiveData) throws NoSuchMethodException {
        UserPassCredentials credentials = World.getInstance().getUserPassCredentials();
        try {
            app.getClass().getMethod(methodName).invoke(app);
            // For web services, calling the method might throw an exception if
            // access is denied.
        } catch (NoSuchMethodException nsm) {
            throw nsm;
        } catch (Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + methodName + "()");
        }
        if (World.getInstance().getMethodProxyMap().get(methodName) != null) {
            log.info("The method: "
                    + methodName
                    + " has already been added to the map, using the existing HTTP logs");
            return;
        }
        World.getInstance().getMethodProxyMap().put(methodName, getProxy().getHistory());
        boolean accessible = getProxy().findInResponseHistory(sensitiveData).size() > 0;
        if (accessible) {
            log.debug("User: " + credentials.getUsername() + " can access resource: " + methodName);
        }
        assertThat("User: " + credentials.getUsername() + " could access resource: " + methodName + " because the text: [" + sensitiveData + "] was present in the responses", accessible, equalTo(true));
    }

    @Given("^the username (.*) is used$")
    public void setUsernameFromExamples(String username) {
        World.getInstance().getUserPassCredentials().setUsername(username);
    }

    @Given("^the password (.*) is used$")
    public void setCredentialsFromExamples(String password) {
        World.getInstance().getUserPassCredentials().setPassword(password);
    }

    @When("the previously recorded HTTP Requests for (.*) are replayed using the current session ID")
    public void areReplayedUsingCSessID(String method) {
        methodName = method;
    }

    @Then("the X-Frame-Options header is either (.*) or (.*)")
    public void checkIfXFrameOptionsHeaderIsSet(String sameOrigin, String deny) {
        String xFrameOptionsValue = Utils.getResponseHeaderValue(World.getInstance().getCurrentHar().getResponse(),Constants.XFRAMEOPTIONS);
        assertThat(Constants.XFRAMEOPTIONS+" header is not set",xFrameOptionsValue,notNullValue());
        assertThat("X-FRAME-Options header is: "+xFrameOptionsValue,Utils.responseHeaderValueIsOneOf(World.getInstance().getCurrentHar().getResponse(), Constants.XFRAMEOPTIONS, new String[]{sameOrigin, deny}), equalTo(true));
    }

    @Given("the access control map for authorised users has been populated")
    public void checkIfMapPopulated() {
        if (World.getInstance().getMethodProxyMap().size() == 0)
            throw new RuntimeException(
                    "Access control map has not been populated.");
    }

    @When("the HTTP requests and responses are recorded")
    public void noActionForRecordingHttpRequestResponses() {
        //HTTP traffic is recorded in checkAccessToResource
    }

    @Then("the string: (.*) should not be present in any of the HTTP responses")
    public void checkNoAccessToResource(String sensitiveData) {
        if (World.getInstance().getMethodProxyMap() == null || World.getInstance().getMethodProxyMap().get(methodName).size() == 0)
            throw new ConfigurationException(
                    "No HTTP messages were recorded for the method: " + methodName);
        findAndSetSessionIds();
        if (app instanceof WebApplication) {
            checkAccessUsingCookieMethod(sensitiveData);
        } else {
            checkAccessUsingAuthTokenMethod(sensitiveData);
        }
    }

    private void checkAccessUsingAuthTokenMethod(String sensitiveData) {
        boolean accessible = false;
        app.getAuthTokenManager().deleteAuthTokens();
        app.getAuthTokenManager().setAuthTokens(World.getInstance().getSessionIds());
        getProxy().clear();
        try {
            app.getClass().getMethod(methodName).invoke(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<HarEntry> results = getProxy().findInResponseHistory(sensitiveData);
        accessible = results != null && results.size() > 0;
        if (!accessible) {
            log.debug("User: " + World.getInstance().getUserPassCredentials().getUsername() + " has no access to resource: " + methodName);
        }
        assertThat(accessible, equalTo(false));
    }

    private void checkAccessUsingCookieMethod(String sensitiveData) {
        boolean accessible = false;
        for (HarEntry entry : World.getInstance().getMethodProxyMap().get(methodName)) {
            if (entry.getResponse().getBodySize() > 0) {
                getProxy().clear();
                HarRequest manual = null;
                try {
                    manual = Utils.replaceCookies(entry.getRequest(), World.getInstance().getSessionIds());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Could not copy Har request");
                }
                getProxy().makeRequest(manual, false); //TODO change this to true once ZAP bug is fixed
                List<HarEntry> results = getProxy().findInResponseHistory(sensitiveData);
                accessible = results != null && results.size() > 0;
                if (accessible) break;
            }
        }
        if (!accessible) {
            log.debug("User: " + World.getInstance().getUserPassCredentials().getUsername() + " has no access to resource: " + methodName);
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
        World.getInstance().setCurrentHar(history.get(0));
    }

    @Then("the Strict-Transport-Security header is set")
    public void checkIfHSTSHeaderIsSet() {
        assertThat("No "+Constants.HSTS+" header found",Utils.responseContainsHeader(World.getInstance().getCurrentHar().getResponse(), Constants.HSTS), equalTo(true));
    }

    @Then("the HTTP (.*) header has the value: (.*)")
    public void checkHeaderValue(String name, String value) {
        HarEntry currentHar = World.getInstance().getCurrentHar();
        assertNotNull("No HTTP header named: " + name + " was found.", Utils.getResponseHeaderValue(currentHar.getResponse(), name));
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), name), equalTo(value));
    }


    @Then("the Access-Control-Allow-Origin header must not be: (.*)")
    public void checkThatAccessControlAllowOriginIsNotStar(String star) {
        assertThat(Utils.getResponseHeaderValue(World.getInstance().getCurrentHar().getResponse(), Constants.XXSSPROTECTION), not(star));
    }

    @When("the following URLs are visited and their HTTP responses recorded")
    public void accessSecureBaseUrlAndRecordHTTPResponse(List<String> urls) {
        for (String url : urls) {
            if (!World.getInstance().isHttpHeadersRecorded()) {
                enableLoggingDriver();
                clearProxy();
                if ("baseUrl".equalsIgnoreCase(url)) url = Config.getInstance().getBaseUrl();
                ((Browser) app.getAuthTokenManager()).getUrl(url);
                recordFirstHarEntry();
                World.getInstance().setHttpHeadersRecorded(true);
            }
        }
    }

    @And("^the default username$")
    public void theDefaultUsername() throws Throwable {
        World.getInstance().getUserPassCredentials().setUsername(((UserPassCredentials) Config.getInstance().getDefaultCredentials()).getUsername());
    }

    @When("^the default password$")
    public void theDefaultPassword() throws Throwable {
        World.getInstance().getUserPassCredentials().setPassword(((UserPassCredentials) Config.getInstance().getDefaultCredentials()).getPassword());
    }

}


