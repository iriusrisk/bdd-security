package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;



@CucumberOptions(
        features = {
                "src/test/resources/stories/authentication.feature"
        }
)
public class AuthenticationTestSuite extends WebDriverTestSuite {

}