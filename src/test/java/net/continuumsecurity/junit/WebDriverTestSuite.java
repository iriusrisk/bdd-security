package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

/**
 * Created by stephen on 1/02/16.
 */

public class WebDriverTestSuite extends BaseCucumberTestSuite {
    @AfterClass
    public static void tearDown() {
        DriverFactory.quitAll();
    }
}
