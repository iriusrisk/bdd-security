package net.continuumsecurity.testng;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.InfrastructureSteps;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;


public class SslTest {
    protected InfrastructureSteps webAppSteps = new InfrastructureSteps();

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }



}

