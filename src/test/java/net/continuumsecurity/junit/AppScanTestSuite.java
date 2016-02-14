package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;


@CucumberOptions(
        features = {
                "src/test/resources/features/app_navigate.feature",
                "src/test/resources/features/app_scan.feature"
        }
)
public class AppScanTestSuite extends BaseCucumberTestCase {

}
