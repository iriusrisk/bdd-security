package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;



@CucumberOptions(
        features = {
                "src/test/resources/features/authentication.feature"
        },
        tags = {"@cwe-295-auth"}
)
public class AuthenticationTestSuite extends BaseCucumberTestCase {

}