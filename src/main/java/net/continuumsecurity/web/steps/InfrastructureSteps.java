package net.continuumsecurity.web.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.Utils;
import net.continuumsecurity.scanner.PortResult;
import net.continuumsecurity.scanner.PortScanner;
import net.continuumsecurity.utils.TestSSL;
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
    TestSSL testSSL;
    String targetHost;
    PortScanner portScanner;
    List<PortResult> portScanResults;
    List<Integer> selectedPorts;
    List<Integer> expectedPorts;

    public InfrastructureSteps() {

    }

    @BeforeStory
    public void setup() {

    }

    @Given("SSL tests have been run on the secure base Url")
    public void runSSLTestsOnSecureBaseUrl() throws IOException {
        if (testSSL == null) {
            testSSL = new TestSSL();
            URL url = new URL(Config.getBaseSecureUrl());
            int port = url.getPort();
            if (port == -1) port = 443;
            testSSL.test(url.getHost(), port);
        }
    }

    @Then("the service must not support SSL compression")
    public void sslServiceNotVulnerableToCRIME() {
        assertThat(testSSL.isVulnCRIME(), is(false));
    }

    @Then("the service must not be vulnerable to the BEAST attack")
    public void sslServiceNotVulnerableToBEAST() {
        assertThat(testSSL.isVulnBEAST(), is(false));
    }

    @Then("the minimum ciphers strength must be 128 bit")
    public void sslMinimum128bitCiphers() {
        assertThat(testSSL.getMinEncryptionStrength(), greaterThanOrEqualTo(3));
    }

    @Then("SSL version 2 must not be supported")
    public void sslNoV2() {
        boolean isV2 = false;
        for (String version : testSSL.getSupportedProtocols()) {
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
        for (String version : testSSL.getSupportedProtocols()) {
            if (version.contains(protocol)) {
                found = true;
            }
        }
        assertThat(testSSL.getSupportedProtocols().toString(), found, equalTo(true));
    }

    @Then("$cipher ciphers must not be supported")
    public void sslNoCipher(@Named("cipher") String cipher) {
        assertThat(testSSL.getSupportedCiphers().toString(), Utils.mapOfStringListContainsString(testSSL.getSupportedCiphers(), cipher), is(false));
    }

    //@Then("a $cipherType cipher should be supported")
    @Then("a $cipher cipher must be enabled")
    public void sslSupportAtLeastOneCipher(@Named("cipher") String cipher) {
        assertThat(testSSL.getSupportedCiphers().toString(), Utils.mapOfStringListContainsString(testSSL.getSupportedCiphers(), cipher), is(true));
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
        selectedPorts = new ArrayList<>();
        for (PortResult result : portScanResults) {
            if (result.getState().equals(PortResult.PortState.fromString(state))) {
                selectedPorts.add(result.getPort());
            }
        }
    }

    @Then("only the following ports should be open: $portsTable")
    public void checkOpenPorts(ExamplesTable portsTable) {
        expectedPorts = new ArrayList<>();
        for (Map<String,String> row : portsTable.getRows()) {
             expectedPorts.add(Integer.parseInt(row.get("port")));
        }
        assertThat("Only the expected ports are open",selectedPorts, hasItems(expectedPorts.toArray(new Integer[expectedPorts.size()])));
    }

}
