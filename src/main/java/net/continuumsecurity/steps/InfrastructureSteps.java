package net.continuumsecurity.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.continuumsecurity.Port;
import net.continuumsecurity.scanner.PortScanner;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class InfrastructureSteps {
    Logger log = Logger.getLogger(InfrastructureSteps.class);
    String targetHost;
    PortScanner portScanner;
    List<Port> portScanResults;
    List<Integer> selectedPorts;
    List<Integer> expectedPorts;

    @Given("the target host name (.*)")
    public void setTargetHost(String hostname) throws MalformedURLException {
        targetHost = hostname;
    }

    @When("^TCP ports from (\\d+) to (\\d+) are scanned using (\\d+) threads and a timeout of (\\d+) milliseconds$")
    public void scanPorts(int from, int to, int threads, int timeout) throws ExecutionException, InterruptedException {
        portScanner = new PortScanner(targetHost,from,to,threads,timeout);
        portScanResults = portScanner.scan();
    }

    @When("the (.*) ports are selected")
    public void selectPorts(String state) {
        selectedPorts = new ArrayList<Integer>();
        for (Port result : portScanResults) {
            if (result.getState().equals(Port.State.fromString(state))) {
                selectedPorts.add(result.getNumber());
            }
        }
    }

    @Then("the ports should be (.*)")
    public void checkPortStates(String csvPorts) {
        expectedPorts = new ArrayList<Integer>();
        for (String portAsString : csvPorts.split(",")) {
            expectedPorts.add(Integer.parseInt(portAsString));
        }
        assertThat("Only the expected ports are open",selectedPorts, containsInAnyOrder(expectedPorts.toArray(new Integer[expectedPorts.size()])));
    }

    public List<Integer> getSelectedPorts() {
        return selectedPorts;
    }
}
