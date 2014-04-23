package net.continuumsecurity.web.steps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.continuumsecurity.Config;
import net.continuumsecurity.ReportClient;
import net.continuumsecurity.ScanClient;
import net.continuumsecurity.model.Issue;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.testng.internal.Utils;

import javax.security.auth.login.LoginException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class NessusScanningSteps {
	Logger log = Logger.getLogger(NessusScanningSteps.class);
    ScanClient scanClient;
    ReportClient reportClient;
    String policyName;
    List<String> hostNames = new ArrayList<String>();
    String scanUuid;
    String scanName;
    String username,password;
    Map<Integer,Issue> issues;
    String nessusUrl;

    
    @Given("a nessus server at $nessusUrl")
    public void createNessusClient(String url) {
        nessusUrl = url;
    	scanClient = new ScanClient(url);
    }

    @Given("the nessus username $username and the password $password")
    public void setNessusCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Given("the scanning policy named $policyName")
    public void setScanningPolicy(String policyName) {
    	this.policyName = policyName;
    }
    
    @Given("the target hosts $hosts")
    public void setTargetHosts(ExamplesTable hostsTable) throws MalformedURLException {
    	for (Map<String,String> host : hostsTable.getRows()) {
    		String hostname = host.get("hostname");
    		if ("baseUrl".equalsIgnoreCase(hostname)) {
    			URL url = new URL(Config.getBaseUrl());
    			hostname = url.getHost();
    		} 
    		hostNames.add(hostname);    		
    	}
    }

    @When("the scanner is run with scan name $scanName")
    public void runScan(String scanName) throws LoginException {
        this.scanName = scanName;
        scanClient.login(username,password);
        scanUuid = scanClient.newScan(scanName,policyName, Utils.join(hostNames,","));
    }

    @When("the list of issues is stored")
    public void storeIssues() throws LoginException {
        waitForScanToComplete(scanName);
        reportClient = new ReportClient(nessusUrl);
        reportClient.login(username,password);
        issues = reportClient.getAllIssuesSortedByPluginId(scanUuid);
    }

    @When("the following false positives are removed $falsep")
    public void removeFalsePositives(ExamplesTable falsePositivesTable) {
        for (Map<String,String> row : falsePositivesTable.getRows()) {
            Integer pluginId = Integer.parseInt(row.get("PluginID"));
            String hostname = row.get("Hostname");

            Issue issue = issues.get(pluginId);
            if (issue != null) {
                issue.getHosts().remove(hostname);
                if (issue.getHosts().size() == 0) {
                    issues.remove(pluginId);
                }
            }
        }
    }

    @Then("no severity: $severity or higher issues should be present")
    public void verifyRiskOfIssues(int severity) {
        List<Issue> notable = new ArrayList<Issue>();
        for (Issue issue : issues.values()) {
            if (issue.getSeverity() >= severity) {
                notable.add(issue);
            }
        }
        assertThat(notable,empty());
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
