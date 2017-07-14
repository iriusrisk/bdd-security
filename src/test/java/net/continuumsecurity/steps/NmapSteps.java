package net.continuumsecurity.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Ports;
import org.nmap4j.data.nmaprun.Host;

public class NmapSteps {
    Nmap4j nmap4j;

    @Given("an nmap instance")
    public void sayHello() {
        nmap4j = new Nmap4j( "/usr/local" ) ;
        nmap4j.includeHosts( "localhost" ) ;
        nmap4j.excludeHosts( "192.168.1.110" ) ;
        nmap4j.addFlags( "-sV -p22" ) ;

    }

    @When("the scan is executed")
    public void executeScan() {
        try {
            nmap4j.execute() ;
        } catch (NMapInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Nmap: "+nmap4j.getExecutionResults().getErrors());
        } catch (NMapExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing Nmap: "+nmap4j.getExecutionResults().getErrors());
        }
        NMapRun nmapRun = nmap4j.getResult();
        for (Host host : nmapRun.getHosts()) {
            Ports ports = host.getPorts();
        }
    }
}
