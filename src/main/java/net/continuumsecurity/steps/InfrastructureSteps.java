package net.continuumsecurity.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.scanner.PortResult;
import net.continuumsecurity.scanner.PortScanner;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InfrastructureSteps {
    Logger log = Logger.getLogger(InfrastructureSteps.class);
    String targetHost;
    PortScanner portScanner;
    List<PortResult> portScanResults;
    List<Integer> selectedPorts;
    List<Integer> expectedPorts;

    @Given("the target host from the base URL")
    public void setTargetHostFromBaseURL() throws MalformedURLException {
        targetHost = new URL(Config.getInstance().getBaseUrl()).getHost();
    }

    @When("TCP ports from $from to $to are scanned using $threads threads and a timeout of $timeout milliseconds")
    public void scanPorts(int from, int to, int threads, int timeout) throws ExecutionException, InterruptedException {
        portScanner = new PortScanner(targetHost,from,to,threads,timeout);
        portScanResults = portScanner.scan();
    }

    @When("the $state ports are selected")
    public void selectPorts(String state) {
        selectedPorts = new ArrayList<Integer>();
        for (PortResult result : portScanResults) {
            if (result.getState().equals(PortResult.PortState.fromString(state))) {
                selectedPorts.add(result.getPort());
            }
        }
    }

    @Then("only the following ports should be selected $portsTable")
    public void checkOpenPorts(ExamplesTable portsTable) {
        expectedPorts = new ArrayList<Integer>();
        for (Map<String,String> row : portsTable.getRows()) {
             expectedPorts.add(Integer.parseInt(row.get("port")));
        }
        assertThat("Only the expected ports are open",selectedPorts, hasItems(expectedPorts.toArray(new Integer[expectedPorts.size()])));
    }

    public List<Integer> getSelectedPorts() {
        return selectedPorts;
    }
}
