package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/",
    //Enable tags to run specific Scenarios and/or Features
    tags = {"@authorisation"}
)
public class AllFeaturesTest extends BaseCucumberTest {
}
