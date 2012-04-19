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

import java.lang.reflect.Method;
import java.util.Map;

import net.continuumsecurity.burpclient.BurpClient;
import net.continuumsecurity.burpclient.ScanPolicy;
import net.continuumsecurity.restyburp.model.ScanIssueBean;
import net.continuumsecurity.restyburp.model.ScanIssueList;
import net.continuumsecurity.web.Config;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.BurpFactory;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.reporting.BurpAnalyser;
import net.continuumsecurity.web.reporting.ScannerReporter;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class AutomatedScanningSteps {
	Logger log = Logger.getLogger(AutomatedScanningSteps.class);
	BurpClient burp;
	WebApplication app;
	Map<String, String> scanPolicy;
	ScanIssueList issues;
	String vulnName;
	private boolean navigated = false;
	ScannerReporter reporter;

	public AutomatedScanningSteps(WebApplicationSteps webAppSteps) {
		this.app = webAppSteps.app;
		this.burp = BurpFactory.getBurp();
		reporter = new ScannerReporter();
	}

	@BeforeStory
	public void createScanner() {
		log.debug("Resetting Burp state");
		burp.reset();
		scanPolicy = null;
	}

	@Given("scanning of all injection points is enabled")
	public void enableAllInjectionPoints() {
		assert scanPolicy != null : "scanPolicy first needs to be created before injection points are set.";
		log.debug(" enabling all injection points");
		scanPolicy.put(ScanPolicy.INSERT_BODY_PARAMS, "true");
		scanPolicy.put(ScanPolicy.INSERT_AMF_PARAMS, "true");
		scanPolicy.put(ScanPolicy.INSERT_COOKIES, "true");
		scanPolicy.put(ScanPolicy.INSERT_HEADERS, "true");
		scanPolicy.put(ScanPolicy.INSERT_PARAM_NAME, "true");
		scanPolicy.put(ScanPolicy.INSERT_URL_PARAMS, "true");
	}

	@Given("scanning of REST-style URL parameters is enabled")
	public void enableRESTparams() {
		assert scanPolicy != null : "scanPolicy first needs to be created before injection points are set.";
		log.debug(" enabling REST-style parameters");

		scanPolicy.put(ScanPolicy.INSERT_REST_PARAMS, "true");
	}

	@Given("a passive scanning policy")
	public void setupPassivePolicy() {
		vulnName = "passive security";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.FORMS,
				ScanPolicy.LINKS, ScanPolicy.PARAMS, ScanPolicy.COOKIES,
				ScanPolicy.MIME, ScanPolicy.CACHING,
				ScanPolicy.INFO_DISCLOSURE, ScanPolicy.VIEWSTATE).getPolicy();
	}

	@Given("an SQL injection scanning policy")
	public void setupSQLinjectionPolicy() {
		vulnName = "SQL injection";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.SQL_INJECTION,
				ScanPolicy.SQL_INJECTION_BOOLEAN,
				ScanPolicy.SQL_INJECTION_ERROR).getPolicy();// ,
		// ScanPolicy.SQL_INJECTION_TIME).policy
	}

	@Given("an LDAP injection scanning policy")
	public void setupLDAPinjectionPolicy() {
		vulnName = "LDAP injection";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.LDAP_INJECTION)
				.getPolicy();
	}

	@Given("an XML injection scanning policy")
	public void setupXMLinjectionPolicy() {
		vulnName = "XML injection";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.XML_SOAP_INJECTION)
				.getPolicy();
	}

	@Given("a policy containing miscellaneous header and server checks")
	public void setupMiscPolicy() {
		vulnName = "general security";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.PATH_TRAVERSAL,
				ScanPolicy.HEADER_INJECTION, ScanPolicy.HEADER_MANIPULATION,
				ScanPolicy.SERVER_ISSUES).getPolicy();
	}

	@Given("a Cross Site Scripting scanning policy")
	public void setupXSSPolicy() {
		vulnName = "Cross Site Scripting";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.REFLECTED_XSS,
				ScanPolicy.STORED_XSS).getPolicy();
	}

	@Given("a command injection scanning policy")
	public void setupCommandInjectionPolicy() {
		vulnName = "command injection";
		log.debug(" configuring " + vulnName + " policy");

		scanPolicy = new ScanPolicy().enable(ScanPolicy.COMMAND_INJECTION,
				ScanPolicy.COMMAND_INJECTION_INFORMED,
				ScanPolicy.COMMAND_INJECTION_BLIND).getPolicy();
	}

	@When("the application is navigated")
	public void navigateApp() throws Exception {
		// Navigate through the app and record the traffic through the
		// scanner
		for (Method method : app.getScannableMethods()) {
			app.setDriver(DriverFactory.getDriver(Config.getBurpDriver()));
			log.debug("Navigating method: "+method.getName());
			app.getClass().getMethod(method.getName(), null).invoke(app, null);
		}
		navigated = true;
	}

	@When("the scanner is run")
	public void runScanner(@Named("id") String reference) throws Exception {
		if (!navigated)
			navigateApp();
		log.debug("running scanner for scenario: " + reference);
		burp.setConfig(scanPolicy);
		int scanId = burp.scan(Config.getBaseUrl());
		int complete = 0;
		while (complete < 100) {
			complete = burp.percentComplete(scanId);
			log.debug("Scan is " + complete + "% complete.");
			Thread.sleep(3000);
		}
		issues = BurpAnalyser.instance().filter(burp.getIssueList(scanId));
		log.debug(issues.getIssues().size() + " security issues were found.");
		reporter.write(reference, issues);
	}

	@Then("no vulnerabilities should be present")
	public void checkVulnerabilities() {
		log.debug("checking for vulnerabilities");
		String detail = "";
		if (issues.getIssues().size() != 0) {
			for (ScanIssueBean issue : issues.getIssues()) {
				detail = detail + issue.getUrl() + "\n"
						+ issue.getIssueDetail() + "\n\n";
			}
		}
		assertThat(issues.getIssues().size() + " " + vulnName
				+ " vulnerabilities found.\n" + detail, issues.getIssues().size(),
				equalTo(0));
	}
}
