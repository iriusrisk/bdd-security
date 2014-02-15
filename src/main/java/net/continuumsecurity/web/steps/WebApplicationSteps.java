/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2012 Stephen de Vries`
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
package net.continuumsecurity.web.steps;

import edu.umass.cs.benchlab.har.HarCookie;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarRequest;
import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.proxy.LoggingProxy;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.FakeCaptchaSolver;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.ProxyFactory;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    Map<String, List<HarEntry>> methodProxyMap = new HashMap<String, List<HarEntry>>();
    List<HarEntry> recordedEntries;
    WebElement currentElement;

    public WebApplicationSteps() {

    }

    @BeforeStory
    public void setup() {
        createApp();
    }

    /*
     * This has to be called explicitly when using an examples table in order to
     * start with a fresh browser instance, because @BeforeScenario is only
     * called once for the whole scenario, not each example.
     */
    @Given("a fresh application")
    public void createApp() {
        app = Config.createApp();
        app.enableDefaultClient();
        assert app.getWebDriver() != null;
        app.getWebDriver().manage().deleteAllCookies();
    }

    @BeforeScenario
    public void createAppAndCredentials() {
        createApp();
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

    @Given("the username <username>")
    public void setUsernameFromExamples(@Named("username") String username) {
        credentials.setUsername(username);
    }

    @Given("an invalid username")
    public void setInvalidUsername() {
        credentials.setUsername(Config.getIncorrectUsername());
    }

    @Given("the password <password>")
    public void setCredentialsFromExamples(@Named("password") String password) {
        credentials.setPassword(password);
    }

    @Given("an incorrect password")
    public void incorrectPassword() {
        credentials.setPassword(Config.getIncorrectPassword());
    }

    @When("the user logs in from a fresh login page")
    public void loginFromFreshPage() {
        createApp();
        openLoginPage();
        loginWithSetCredentials();
    }

    @Then("the user is logged in")
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

    @When("the request for the login form is submitted over HTTP")
    public void submitLoginOverHttp() {
        String url = currentHar.getRequest().getUrl().replaceFirst("https", "http");
        ((WebApplication) app).getWebDriver().get(url);

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

    @Given("a browser configured to use an intercepting proxy")
    public void enableLoggingDriver() {
        app.enableHttpLoggingClient();
    }

    @Given("the proxy logs are cleared")
    @When("the proxy logs are cleared")
    public void clearProxy() {
        proxy = ProxyFactory.getLoggingProxy();
        proxy.clear();
    }

    @Given("the HTTP request-response containing the default credentials is inspected")
    public void findRequestWithPassword() throws UnsupportedEncodingException {
        String passwd = URLEncoder.encode(credentials.getPassword(), "UTF-8");
        String username = URLEncoder.encode(credentials.getUsername(), "UTF-8");
        List<HarEntry> requests = proxy.findInRequestHistory(credentials.getPassword());
        if (requests == null || requests.size() == 0)
            throw new StepException(
                    "Could not find HTTP request with credentials: "
                            + credentials.getUsername() + " "
                            + credentials.getPassword());
        currentHar = requests.get(0);
    }

    @Then("the protocol should be HTTPS")
    public void protocolHttps() {
        assertThat(currentHar.getRequest().getUrl(),currentHar.getRequest().getUrl().substring(0, 4),equalTo("https"));
    }

    @Given("the HTTP request-response containing the login form")
    public void findResponseWithLoginform() throws UnsupportedEncodingException {
        String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
        List<HarEntry> responses = proxy.getHistory();
        responses = proxy.findInResponseHistory(regex);
        if (responses == null || responses.size() == 0)
            throw new StepException(
                    "Could not find HTTP response with password form using regex: "
                            + regex);
        currentHar = responses.get(0);
    }

    @Then("the protocol of the current URL should be HTTPS")
    public void protocolBrowserUrlHttps() {
        String currentUrl = ((WebApplication) app).getWebDriver().getCurrentUrl();
        assertThat(currentUrl,currentUrl.substring(0, 4), equalToIgnoringCase("https"));
    }

    @Then("the response status code should start with 3")
    public void statusCode3xx() {
        assertThat(Integer.toString(currentHar.getResponse().getStatus()).substring(0, 1), equalTo("3"));
    }

    @Given("the value of the session cookie is noted")
    public void getSessionIds() {
        Config.instance();
        for (String name : Config.getSessionIDs()) {
            Cookie cookie = app.getCookieByName(name);
            if (cookie != null)
                sessionIds.add(cookie);
        }
    }

    @Then("the value of the session cookie issued after authentication should be different from that of the previously noted session ID")
    public void compareSessionIds() {
        Config.instance();
        for (String name : Config.getSessionIDs()) {
            Cookie initialSessionCookie = findCookieByName(sessionIds, name);
            if (initialSessionCookie != null) {
                String existingCookieValue = findCookieByName(sessionIds, name)
                        .getValue();
                assertThat(app.getCookieByName(name).getValue(),
                        not(initialSessionCookie.getValue()));
            } else if (app.getCookieByName(name).getValue() == null) {
                throw new RuntimeException(
                        "No session IDs found after login with name: " + name);
            }
        }
    }

    @Then("the session cookie should have the secure flag set")
    public void sessionCookiesSecureFlag() {
        Config.instance();
        for (String name : Config.getSessionIDs()) {
            assertThat(app.getCookieByName(name).isSecure(), equalTo(true));
        }
    }

    @Then("the session cookie should have the httpOnly flag set")
    public void sessionCookiesHttpOnlyFlag() {
        Config.instance();
        int numCookies = Config.getSessionIDs().size();
        int cookieCount = 0;
        for (HarEntry entry : proxy.getHistory()) {
            for (String name : Config.getSessionIDs()) {
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
        String xpath = "//input[@type='password']";
        List<WebElement> passwds = ((WebApplication) app).getWebDriver().findElements(
                By.xpath(xpath));
        if (passwds.size() != 1) throw new UnexpectedContentException("Found "+passwds.size()+" password fields using XPath: "+xpath);
        currentElement = passwds.get(0);
    }


    @When("the login form is inspected")
    public void selectLoginFormElement() {
        String xpath = "//form[.//input[@type='password']]";
        List<WebElement> loginForms = ((WebApplication) app).getWebDriver().findElements(
                By.xpath(xpath));
        if (loginForms.size() != 1) throw new UnexpectedContentException("Found "+loginForms.size()+" login forms using XPath: "+xpath);
        currentElement = loginForms.get(0);
    }

    @Then("it should have the autocomplete attribute set to 'off'")
    public void thenTheLogonFormShouldHaveTheAutocompleteDirectiveSetToOff() {
        assertThat("Autocomplete set to off", currentElement.getAttribute("autocomplete"), equalToIgnoringCase("off"));
    }

    @Then("no exceptions are thrown")
    public void doNothing() {

    }

    @Then("the CAPTCHA request should be present")
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
        ((ICaptcha) app).setCaptchaHelper(new FakeCaptchaSolver(app));
    }

    @When("the password recovery feature is requested")
    public void submitPasswordRecovery() {
        ((IRecoverPassword) app).submitRecover(Config.instance().getUsers()
                .getAll().get(0).getRecoverPasswordMap());
    }

    @Then("the CAPTCHA should be presented again")
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
        methodProxyMap.put(method, proxy.getHistory());
    }

    @Given("the access control map for authorised users has been populated")
    public void checkIfMapPopulated() {
        if (methodProxyMap.size() == 0)
            throw new RuntimeException(
                    "Access control map has not been populated.");
    }

    @Then("when they access the restricted resource: <method> they should see the string: <sensitiveData>")
    public void checkAccessToResource(
            @Named("sensitiveData") String sensitiveData,
            @Named("method") String method) {
        try {
            app.getClass().getMethod(method).invoke(app);
            // For web services, calling the method might throw an exception if
            // access is denied.
        } catch (Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + method + "()");
        }
        if (methodProxyMap.get(method) != null) {
            log.info("The method: "
                    + method
                    + " has already been added to the map, using the existing HTTP logs");
            return;
        }
        methodProxyMap.put(method, proxy.getHistory());
        boolean accessible = proxy.findInResponseHistory(sensitiveData).size() > 0;
        if (accessible) {
            log.debug("User: " + credentials.getUsername() + " can access resource: " + method);
        }
        assertThat("User: " + credentials.getUsername() + " could access resource: " + method + " because the text: " + sensitiveData + " was present in the responses", accessible, equalTo(true));
    }

    @When("they access the restricted resource: <method> and the response that contains the string: <sensitiveData> is recorded")
    public void recordSensitiveResponse(
            @Named("sensitiveData") String sensitiveData,
            @Named("method") String method) {
        try {
            app.getClass().getMethod(method).invoke(app);
            // For web services, calling the method might throw an exception if
            // access is denied.
        } catch (Exception e) {
            fail("User with credentials: " + credentials.getUsername() + " "
                    + credentials.getPassword()
                    + " could not access the method: " + method + "()");
        }
        recordedEntries = proxy.findInResponseHistory(sensitiveData);
        assertThat("The string: " + sensitiveData + " was not found in the HTTP responses", recordedEntries.size(), greaterThan(0));
        currentHar = recordedEntries.get(0);
    }

    @Then("the HTTP Cache-control header should contain the values:")
    public void checkCacheControlHeaders(@Named("value") String value) {
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), Constants.CACHECONTROL), equalTo(value));
    }

    @Then("when they access the restricted resource: <method> they should not see the string: <sensitiveData>")
    public void checkNoAccessToResource(
            @Named("sensitiveData") String sensitiveData,
            @Named("method") String method) {
        if (methodProxyMap == null || methodProxyMap.get(method).size() == 0)
            throw new ConfigurationException(
                    "No HTTP messages were recorded for the method: " + method);
        boolean accessible = false;
        getSessionIds();
        for (HarEntry entry : methodProxyMap.get(method)) {
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
                proxy.clear();
                List<HarEntry> results = proxy.makeRequest(manual, true);
                results = proxy.findInResponseHistory(sensitiveData);
                accessible = results != null && results.size() > 0;
                if (accessible) break;

            }
        }
        if (!accessible) {
            log.debug("User: " + credentials.getUsername() + " has no access to resource: " + method);
        }
        assertThat("Resource: " + method + " can not be accessed by user: " + credentials.getUsername() + " because the text: " + sensitiveData + " was not present in the responses.",
                accessible, equalTo(false));
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
        List<HarEntry> history = proxy.getHistory();
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

    @When("the secure base Url for the application is accessed")
    public void openBaseSecureUrl() {
        app.getWebDriver().get(Config.getBaseSecureUrl());
    }

    @Then("the Access-Control-Allow-Origin header must not be: $value")
    public void checkThatAccessControlAllowOriginIsNotStar(@Named("value") String star) {
        assertThat(Utils.getResponseHeaderValue(currentHar.getResponse(), Constants.XXSSPROTECTION), not(star));
    }

}
