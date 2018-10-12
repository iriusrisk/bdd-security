package net.continuumsecurity.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.continuumsecurity.*;
import net.continuumsecurity.v5.model.Issue;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class NessusScanningSteps {
    Logger log = Logger.getLogger(NessusScanningSteps.class);
    ScanClient scanClient;
    ReportClient reportClient;
    String policyName;
    List<String> hostNames = new ArrayList<String>();
    String scanUuid;
    String scanIdentifierForStatus;
    String username, password;
    Map<Integer, Issue> issues;
    String nessusUrl;
    int nessusVersion;
    boolean ignoreHostNamesInSSLCert = false;

    @Given("a nessus API client that accepts all hostnames in SSL certificates")
    public void ignoreHostNamesInSSLCert() {
        ignoreHostNamesInSSLCert = true;
    }

    @Given("a nessus version (\\d+) server at (https?:\\/\\/.+)$")
    public void createNessusClient(int version, String url) {
        nessusUrl = url;
        nessusVersion = version;
        scanClient = ClientFactory.createScanClient(url, nessusVersion, ignoreHostNamesInSSLCert);
    }

    @Given("the nessus username (.*) and the password (.*)$")
    public void setNessusCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Given("the scanning policy named (.*)$")
    public void setScanningPolicy(String policyName) {
        this.policyName = policyName;
    }

    @Given("the target host names")
    public void setTargetHosts(List<String> hosts) throws MalformedURLException {
        hostNames.addAll(hosts);
    }

    @When("^the scanner is run with scan name (.*)$")
    public void runScan(String scanName) throws LoginException {
        if (username == null) {
            username = Config.getInstance().getNessusUsername();
            password = Config.getInstance().getNessusPassword();
        }
        scanClient.login(username, password);
        scanUuid = scanClient.newScan(scanName, policyName, StringUtils.join(hostNames, ","));
        if (nessusVersion == 5) {
            scanIdentifierForStatus = scanName;
        } else {
            scanIdentifierForStatus = scanUuid;
        }
    }

    @When("the list of issues is stored")
    public void storeIssues() throws LoginException {
        waitForScanToComplete(scanIdentifierForStatus);
        reportClient = ClientFactory.createReportClient(nessusUrl, nessusVersion, ignoreHostNamesInSSLCert);
        reportClient.login(username, password);
        issues = reportClient.getAllIssuesSortedByPluginId(scanUuid);
    }

    @When("the following nessus false positive are removed")
    public void removeFalsePositives(List<NessusFalsePositive> falsePositives) {
        for (NessusFalsePositive falsePositive : falsePositives) {
            Issue issue = issues.get(falsePositive.getPluginId());
            if (issue != null) {
                issue.getHostnames().remove(falsePositive.getHostname());
                if (issue.getHostnames().size() == 0) {
                    issues.remove(falsePositive.getPluginId());
                }
            }
        }
    }

    @Then("no severity: (\\d+) or higher issues should be present")
    public void verifyRiskOfIssues(int severity) {
        List<Issue> notable = new ArrayList<Issue>();
        for (Issue issue : issues.values()) {
            if (issue.getSeverity() >= severity) {
                notable.add(issue);
            }
        }
        assertThat(notable, empty());
    }

    private void waitForScanToComplete(String scanName) {
        while (scanClient.isScanRunning(scanName)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
