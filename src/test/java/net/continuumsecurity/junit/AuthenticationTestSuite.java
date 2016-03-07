package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;



@CucumberOptions(
        features = {
                "src/test/resources/features/authentication.feature"
        },
        tags = {}
)
public class AuthenticationTestSuite extends BaseCucumberTestCase {

}