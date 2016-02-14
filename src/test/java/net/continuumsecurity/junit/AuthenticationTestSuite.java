package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;



@CucumberOptions(
        features = {
                "src/test/resources/features/authentication.feature"
        }
)
public class AuthenticationTestSuite extends BaseCucumberTestCase {

}