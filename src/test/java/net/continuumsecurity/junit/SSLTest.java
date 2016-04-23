package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;

@CucumberOptions(
        features = {
                "src/test/resources/features/ssl.feature"
        },
        tags = {}
)
public class SSLTest extends BaseCucumberTest {
}