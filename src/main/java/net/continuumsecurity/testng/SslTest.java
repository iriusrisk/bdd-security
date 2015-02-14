package net.continuumsecurity.testng;

import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.steps.InfrastructureSteps;
import org.testng.annotations.AfterClass;


public class SslTest {
    protected InfrastructureSteps webAppSteps = new InfrastructureSteps();

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }



}

