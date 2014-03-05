package net.continuumsecurity.web.steps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.continuumsecurity.Config;
import net.continuumsecurity.ReportClient;
import net.continuumsecurity.ScanClient;
import net.continuumsecurity.proxy.ScanningProxy;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.testng.internal.Utils;

import javax.security.auth.login.LoginException;

public class HostScanningSteps {
	Logger log = Logger.getLogger(AppScanningSteps.class);
    ScanClient scanClient;
    ReportClient reportClient;
    String policyName;
    List<String> hostNames = new ArrayList<String>();
    String scanUuid;
    String username,password;
    
    @Given("a nessus server at $nessusUrl")
    public void createNessusClient(String url) {
    	scanClient = new ScanClient(url);
    	reportClient = new ReportClient(url);    	
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
        scanClient.login(username,password);
        scanUuid = scanClient.newScan(scanName,policyName, Utils.join(hostNames,","));
    }


    
    
    
    

}
