package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import net.continuumsecurity.scanner.ZapManager;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

/**
 * Created by stephen on 1/02/16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        format = { "pretty", "html:build/reports/cucumber", "json:build/reports/cucumber.json", "junit:build/reports/cucumber.xml" },
        glue = { "net.continuumsecurity.steps" },
        tags = { "~@skip" }
)
public class BaseCucumberTest {

    @AfterClass
    public static void tearDown() {
        DriverFactory.quitAll();
        ZapManager.getInstance().stopZap();
    }

}
