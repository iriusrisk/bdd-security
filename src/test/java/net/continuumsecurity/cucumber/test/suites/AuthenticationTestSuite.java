package net.continuumsecurity.cucumber.test.suites;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by bogdanmoldovan on 02/02/16.
 */

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"},
                 features = "src/test/resources/stories/authentication.feature")
public class AuthenticationTestSuite {
}
