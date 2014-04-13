package net.continuumsecurity.web.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.Utils;
import net.continuumsecurity.scanner.PortResult;
import net.continuumsecurity.scanner.PortScanner;
import net.continuumsecurity.utils.SSLTester;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InfrastructureSteps {
    Logger log = Logger.getLogger(WebApplicationSteps.class);
    SSLTester sslTester;
    String targetHost;
    PortScanner portScanner;
    List<PortResult> portScanResults;
    List<Integer> selectedPorts;
    List<Integer> expectedPorts;

    @Given("SSL tests have been run on the secure base Url")
    public void runSSLTestsOnSecureBaseUrl() throws IOException {
        if (sslTester == null) {
            sslTester = new SSLTester();
            URL url = new URL(Config.getBaseSecureUrl());
            int port = url.getPort();
            if (port == -1) port = 443;
            sslTester.test(url.getHost(), port);
        }
    }

    @Then("the service must not support SSL compression")
    public void sslServiceNotVulnerableToCRIME() {
        assertThat(sslTester.isVulnCRIME(), is(false));
    }

    @Then("the service must not be vulnerable to the BEAST attack")
    public void sslServiceNotVulnerableToBEAST() {
        assertThat(sslTester.isVulnBEAST(), is(false));
    }

    @Then("the minimum ciphers strength must be 128 bit")
    public void sslMinimum128bitCiphers() {
        assertThat(sslTester.getMinEncryptionStrength(), greaterThanOrEqualTo(3));
    }

    @Then("SSL version 2 must not be supported")
    public void sslNoV2() {
        boolean isV2 = false;
        for (String version : sslTester.getSupportedProtocols()) {
            if (version.contains("SSLv2")) {
                isV2 = true;
                break;
            }
        }
        assertThat(isV2, equalTo(false));
    }

    @Then("$protocol should be supported")
    public void sslSupportProtocol(@Named("protocol") String protocol) {
        boolean found = false;
        for (String version : sslTester.getSupportedProtocols()) {
            if (version.contains(protocol)) {
                found = true;
            }
        }
        assertThat(sslTester.getSupportedProtocols().toString(), found, equalTo(true));
    }

    @Then("$cipher ciphers must not be supported")
    public void sslNoCipher(@Named("cipher") String cipher) {
        assertThat(sslTester.getSupportedCiphers().toString(), Utils.mapOfStringListContainsString(sslTester.getSupportedCiphers(), cipher), is(false));
    }

    @Then("the service should not be vulnerable to the Heartbleed attack")
    public void checkHeartbleed() {
        assertThat("Vulnerable protocols: "+sslTester.getHeartbleedDetails(), sslTester.isVulnHeartbleed(), is(true));
    }

    //@Then("a $cipherType cipher should be supported")
    @Then("a $cipher cipher must be enabled")
    public void sslSupportAtLeastOneCipher(@Named("cipher") String cipher) {
        assertThat(sslTester.getSupportedCiphers().toString(), Utils.mapOfStringListContainsString(sslTester.getSupportedCiphers(), cipher), is(true));
    }

    @Given("the target host from the base URL")
    public void setTargetHostFromBaseURL() throws MalformedURLException {
        targetHost = new URL(Config.getBaseUrl()).getHost();
    }

    @When("TCP ports from $from to $to are scanned using $threads threads and a timeout of $timeout milliseconds")
    public void scanPorts(int from, int to, int threads, int timeout) throws ExecutionException, InterruptedException {
        portScanner = new PortScanner(targetHost,from,to,threads,timeout);
        portScanResults = portScanner.scan();
    }

    @When("the $state ports are selected")
    public void selectOpenPorts(String state) {
        selectedPorts = new ArrayList<Integer>();
        for (PortResult result : portScanResults) {
            if (result.getState().equals(PortResult.PortState.fromString(state))) {
                selectedPorts.add(result.getPort());
            }
        }
    }

    @Then("only the following ports should be open: $portsTable")
    public void checkOpenPorts(ExamplesTable portsTable) {
        expectedPorts = new ArrayList<Integer>();
        for (Map<String,String> row : portsTable.getRows()) {
             expectedPorts.add(Integer.parseInt(row.get("port")));
        }
        assertThat("Only the expected ports are open",selectedPorts, hasItems(expectedPorts.toArray(new Integer[expectedPorts.size()])));
    }



}
