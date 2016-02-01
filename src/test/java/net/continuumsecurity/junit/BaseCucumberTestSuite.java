package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by stephen on 1/02/16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        format = { "pretty", "html:target/cucumber", "json:target/cucumber.json", "junit:target/cucumber.xml" },
        glue = {"net.continuumsecurity.steps"},
        tags = {"~@skip"}
)
public class BaseCucumberTestSuite {
}
