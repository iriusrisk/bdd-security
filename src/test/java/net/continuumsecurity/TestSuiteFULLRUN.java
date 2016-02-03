package net.continuumsecurity;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by bogdanmoldovan on 02/02/16.
 */

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"},
                 features = "src/test/resources/stories/"
//Todo: enable this only if you want to run a single Scenario using its Cucumber Tag
// tags = "@cwe-178-auth"
)
public class TestSuiteFULLRUN {
}
