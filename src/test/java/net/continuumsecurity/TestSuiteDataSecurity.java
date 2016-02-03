package net.continuumsecurity;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by bogdanmoldovan on 02/02/16.
 */

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"},
                 features = "src/test/resources/stories/data_security.feature")
public class TestSuiteDataSecurity {
}
