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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.continuumsecurity.burpclient.BurpClient;
import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.HttpMessageList;
import net.continuumsecurity.restyburp.model.MessageType;
import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.UnexpectedContentException;
import net.continuumsecurity.web.User;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.BurpFactory;
import net.continuumsecurity.web.drivers.DriverFactory;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

public class WebApplicationSteps {
	Logger log = Logger.getLogger(WebApplicationSteps.class);
	public WebApplication app;
	UserPassCredentials credentials;
	HttpMessage currentHttp;
	HttpMessage savedMessage;
	BurpClient burp;
	List<Cookie> sessionIds;
	
	public WebApplicationSteps() {
		
	}
	
	@BeforeStory
	public void setup() {
		createApp();
	}
	
	/*
	 * This has to be called explicitly when using an examples table in order to start with a fresh browser instance, 
	 * because @BeforeScenario is only called once for the whole scenario, not each example.
	 */
	@Given("a fresh application")
	public void createApp() {
		app = Config.createApp(DriverFactory.getDriver(Config.getDefaultDriver()));
	}
	
	@BeforeScenario
	public void createAppAndCredentials() {
		createApp();
		credentials = new UserPassCredentials("", "");
		sessionIds = new ArrayList<Cookie>();
	}
	
	@Given("the login page")
	public void openLoginPage() {
		((ILogin)app).openLoginPage();
	}

	@Given("the default username from: $credentialsTable")
	public void defaultUsername(ExamplesTable credentialsTable) {
		credentials.setUsername(tableToDefaultCredentials(credentialsTable)
				.getUsername());
		log.debug("username=" + credentials.getUsername());
	}

	@Given("the default password from: $credentialsTable")
	@When("the default password is used from: $credentialsTable")
	public void defaultPassword(ExamplesTable credentialsTable) {
		credentials.setPassword(tableToDefaultCredentials(credentialsTable)
				.getPassword());
		log.debug("password=" + credentials.getPassword());
	}

	@When("the user logs in")
	@Given("the user logs in")
	public void loginWithSetCredentials() {
		((ILogin)app).login(credentials);
	}

	@Given("the default user logs in with credentials from: $credentialsTable")
	@When("the default user logs in with credentials from: $credentialsTable")
	public void loginFromTable(ExamplesTable credentialsTable) {
		openLoginPage();
		credentials = tableToDefaultCredentials(credentialsTable);
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

	// Returns just the first row in the users' credentials table
	private UserPassCredentials tableToDefaultCredentials(
			ExamplesTable credentialsTable) {
		assert credentialsTable.getRowCount() > 0 : "user table must have at least 1 row";
		Parameters firstRow = credentialsTable.getRowAsParameters(0);
		String username = firstRow.valueAs("username", String.class);
		String password = firstRow.valueAs("password", String.class);

		return new UserPassCredentials(username, password);
	}
	
	private String findRoleByUsername(String username) {
		User user = Config.instance().getUsers().findByCredential("username", username);
		if (user != null) {
			return user.getDefaultRole();
		}
		return null;
	}

	@Then("the user with the role <role> should be logged in")
	public void isLoggedIn(@Named("role") String role) {
		log.debug("checking whether logged in");
		assertThat(((ILogin)app).isLoggedIn(role), is(true));
	}

	@Then("the user is logged in")
	public void loginSucceedsVariant2() {
		assertThat(((ILogin)app).isLoggedIn(findRoleByUsername(credentials.getUsername())), is(true));
	}

	@Then("login fails")
	@Alias("the user is not logged in")
	public void loginFails() {
		assertThat(((ILogin)app).isLoggedIn(findRoleByUsername(credentials.getUsername())), is(false));
	}

	@When("the case of the password is changed")
	public void loginWithWrongCasedPassword() {
		String wrongCasePassword = credentials.getPassword().toUpperCase();
		
		if (wrongCasePassword.equals(credentials.getPassword())) {
			wrongCasePassword = credentials.getPassword().toLowerCase();
			if (wrongCasePassword.equals(credentials.getPassword())) {
				throw new RuntimeException("Password doesn't have alphabetic characters, can't run this test.");
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
	public void logout() {
		((ILogout)app).logout();
	}
	
	@Given("an HTTP logging driver")
	public void setBurpDriver() {
		app.setDriver(DriverFactory.getDriver(Config.getBurpDriver()));
	}
	
	@Given("clean HTTP logs")
	public void resetBurp() {
		burp = BurpFactory.getBurp();
		burp.reset();
	}
	
	@Given("the HTTP request-response containing the default credentials")
	public void findRequestWithPassword() throws UnsupportedEncodingException {
		String passwd = URLEncoder.encode(credentials.getPassword(),"UTF-8");
		String username = URLEncoder.encode(credentials.getUsername(),"UTF-8");
		HttpMessageList messageList = new HttpMessageList();
		messageList.setMessages(burp.findInRequestHistory(passwd));
		List<HttpMessage> requests = messageList.findInMessages(username, MessageType.REQUEST);
		if (requests == null || requests.size() == 0) throw new StepException("Could not find HTTP request with credentials: "+credentials.getUsername()+" "+credentials.getPassword());
		currentHttp = requests.get(0); 
	}
	
	@Then("the protocol should be HTTPS")
	public void protocolHttps() {
		assertThat(currentHttp.getProtocol(),equalToIgnoringCase("https"));
	}
	
	@Given("the HTTP request-response containing the login form")
	public void findResponseWithLoginform() throws UnsupportedEncodingException {
		String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
		HttpMessageList messageList = new HttpMessageList();
		messageList.setMessages(burp.findInResponseHistory(regex));
		if (messageList.messages == null || messageList.messages.size() == 0) throw new StepException("Could not find HTTP response with password form using regex: "+regex);
		currentHttp = messageList.messages.get(0); 
	}
	
	@Given("the request-response is saved")
	public void saveCurrentHttp() {
		savedMessage = new HttpMessage(currentHttp);
	}
	
	@Then("the protocol of the current URL should be HTTPS")
	public void protocolUrlHttps() {
		log.debug("URL of login page: "+app.getDriver().getCurrentUrl());
		assertThat(app.getDriver().getCurrentUrl().substring(0, 4),equalToIgnoringCase("https"));
	}
	
	@Then("the response should be the same as the saved response from the invalid username")
	public void compareResponses() {
		assertThat(savedMessage.getStatusCode(),equalTo(currentHttp.getStatusCode()));
		
		String incorrectUsernameResponse = new String(savedMessage.getResponse()).replaceAll(Config.getIncorrectUsername(), "");
		String correctUsernameResponse = new String(currentHttp.getResponse()).replaceAll(Config.instance().getUsers().getDefaultCredentials().get("username"),"");
		assertThat(incorrectUsernameResponse,equalTo(correctUsernameResponse));
	}
	
	@Then("the response status code should start with 3")
	public void statusCode3xx() {
		assertThat(Short.toString(currentHttp.getStatusCode()).substring(0, 1),equalTo("3"));
	}
	
	@Given("the session cookies")
	public void getSessionIds() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			sessionIds.add(app.getDriver().manage().getCookieNamed(name));
		}
	}
	
	@Then("the session cookies after authentication should be different from those issued before")
	public void compareSessionIds() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			String existingCookieValue = findCookieByName(sessionIds,name).getValue();
			assertThat(app.getDriver().manage().getCookieNamed(name).getValue(),not(existingCookieValue));
		}
	}
	
	@Then("the session cookies should have the secure flag set")
	public void sessionCookiesSecureFlag() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			assertThat(app.getDriver().manage().getCookieNamed(name).isSecure(),equalTo(true));
		}
	}
	
	@Then("the session cookies should have the httpOnly flag set")
	public void sessionCookiesHttpOnlyFlag() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			//Welcome to Java's regex matching.  All we want to do is a case insensitive match for httponly in the cookie string
			Pattern pattern = Pattern.compile(".*httponly.*", Pattern.CASE_INSENSITIVE);
			assertThat(pattern.matcher(app.getDriver().manage().getCookieNamed(name).toString()).matches(),equalTo(true));
		}
	}
	
	@Then("they should be able to access the restricted resource <method>")
	public void checkAccessToResource(@Named("method") String method) {
		try {
			Page page = (Page)app.getClass().getMethod(method, null).invoke(app, null);
			//If the app is using the PageObjects pattern
			if (page != null) {
				page.verify();
			}
		} catch (UnexpectedContentException e) {
			fail("User with credentials: "+credentials.getUsername()+" "+credentials.getPassword()+" could not access the method: "+method+"()");
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		}  
	}
	
	@Then("they should not be able to access the restricted resource <method>")
	public void checkNoAccessToResource(@Named("method") String method) {
		try {
			Page page = (Page)app.getClass().getMethod(method, null).invoke(app, null);
			//If the app is using the PageObjects pattern
			if (page != null) {
				page.verify();
			}
			assertThat("",equalTo("Unauthorised user with credentials: "+credentials.getUsername()+" "+credentials.getPassword()+" could access the method: "+method+"()"));
		} catch (InvocationTargetException e) {
			assertThat(e.getCause() instanceof UnexpectedContentException,is(true));
			
		} catch (Exception e) {
			assertThat("",equalTo(e.getMessage()));
			e.printStackTrace();
		} 
	}
	
	@Then("the password field should have the autocomplete directive set to 'off'")
	public void thenThePasswordFieldShouldHaveTheAutocompleteDirectiveSetTodisabled() {
		WebElement passwd = app.getDriver().findElement(By.xpath("//input[@type='password']"));
		assertThat(passwd.getAttribute("autocomplete"),equalToIgnoringCase("off"));
	}
	
	@Then("no exceptions are thrown")
	public void doNothing() {
		
	}
	
	public WebApplication getWebApplication() {
		return app;
	}
	
	private Cookie findCookieByName(List<Cookie> cookies, String name) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) return cookie;
		}
		return null;
	}
}
