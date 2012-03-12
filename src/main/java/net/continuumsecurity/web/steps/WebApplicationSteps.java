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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.continuumsecurity.burpclient.BurpClient;
import net.continuumsecurity.restyburp.model.HttpRequestResponseBean;
import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.ILogin;
import net.continuumsecurity.web.ILogout;
import net.continuumsecurity.web.Page;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.UnexpectedPageException;
import net.continuumsecurity.web.UserPassCredentials;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.BurpFactory;
import net.continuumsecurity.web.drivers.DriverFactory;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.openqa.selenium.Cookie;

public class WebApplicationSteps {
	Logger log = Logger.getLogger(WebApplicationSteps.class);
	public WebApplication app;
	UserPassCredentials credentials;
	HttpRequestResponseBean currentHttp;
	BurpClient burp;
	List<Cookie> sessionIds;
	
	public WebApplicationSteps() {
		createApp();
	}
	
	public void createApp() {
		app = Config.createApp(DriverFactory.getDriver(Config.getDefaultDriver()));
	}
	
	@BeforeScenario
	public void createAppAndCredentials() {
		log.debug("Creating application ...");
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
	public void loginWithSetCredentials() {
		((ILogin)app).login(credentials);
	}

	@Given("the default user logs in: $credentialsTable")
	public void loginFromTable(ExamplesTable credentialsTable) {
		openLoginPage();
		credentials = tableToDefaultCredentials(credentialsTable);
		loginWithSetCredentials();
	}
	
	@Given("the username <username>")
	public void setUsernameFromExamples(@Named("username") String username) {
		credentials.setUsername(username);
	}

	@Given("the password <password>")
	public void setCredentialsFromExamples(@Named("password") String password) {
		credentials.setPassword(password);
	}

	@Given("an incorrect password")
	public void incorrectPassword() {
		credentials.setPassword("SDFsdfwjx");
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

	@Then("the user with the role <role> should be logged in")
	@Alias("the user should be logged in")
	public void isLoggedIn(@Named("role") String role) {
		log.debug("checking whether logged in");
		assertThat(((ILogin)app).isLoggedIn(role), is(true));
	}

	@Then("the user is logged in")
	public void loginSucceedsVariant2() {
		assertThat(((ILogin)app).isLoggedIn(null), is(true));
	}

	@Then("login fails")
	@Alias("the user is not logged in")
	public void loginFails() {
		assertThat(((ILogin)app).isLoggedIn(null), is(false));
	}

	@When("the case of the password is changed")
	public void loginWithWrongCasedPassword() {
		String wrongCasePassword;
		if (credentials.getPassword().matches("[a-z]+")) {
			wrongCasePassword = credentials.getPassword().toUpperCase();
		} else if (credentials.getPassword().matches("[A-Z]+")) {
			wrongCasePassword = credentials.getPassword().toLowerCase();
		} else {
			throw new RuntimeException(
					"Password doesn't have alphabetic characters, can't run this test.");
		}
		credentials.setPassword(wrongCasePassword);
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
	
	@Given("the HTTP request containing the password form")
	public void findPasswordForm() {
		currentHttp = burp.findInResponseHistory(".*<input.*?type.*?=.*?password.*");
	}
	
	@Given("the HTTP request-response containing the default credentials")
	public void findRequestWithPassword() {
		currentHttp = burp.findInRequestHistory(".*"+credentials.getUsername()+".*"+credentials.getPassword()+".*");
		if (currentHttp == null) currentHttp = burp.findInRequestHistory(".*"+credentials.getPassword()+".*"+credentials.getUsername()+".*");
		if (currentHttp == null) throw new StepException("Could not find HTTP request with credentials: "+credentials.getUsername()+" "+credentials.getPassword());
	}
	
	@Then("the protocol should be HTTPS")
	public void protocolHttps() {
		assertThat(currentHttp.getProtocol(),equalTo("https"));
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
	
	@Then("the session cookies should be different")
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
			page.verify();
		} catch (UnexpectedPageException e) {
			fail("User with credentials: "+credentials.getUsername()+" "+credentials.getPassword()+" could not access the method: "+method+"()");
		} catch (Exception e) {
			fail(e.getMessage());
			log.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	@Then("they should not be able to access the restricted resource <method>")
	public void checkNoAccessToResource(@Named("method") String method) {
		try {
			Page page = (Page)app.getClass().getMethod(method, null).invoke(app, null);
			page.verify();
			assertThat("",equalTo("Unauthorised user with credentials: "+credentials.getUsername()+" "+credentials.getPassword()+" could access the method: "+method+"()"));
		} catch (InvocationTargetException e) {
			assertThat(e.getCause() instanceof UnexpectedPageException,is(true));
			
		} catch (Exception e) {
			assertThat("",equalTo(e.getMessage()));
			e.printStackTrace();
		} 
	}
	
	private Cookie findCookieByName(List<Cookie> cookies, String name) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) return cookie;
		}
		return null;
	}
}
