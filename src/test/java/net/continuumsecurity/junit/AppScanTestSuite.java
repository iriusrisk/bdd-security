package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;


@CucumberOptions(
        features = {
                "src/test/resources/features/app_scan.feature"
        },
        tags = { "@cwe-89, @cwe-79" }
)
public class AppScanTestSuite extends BaseCucumberTestCase {

}
