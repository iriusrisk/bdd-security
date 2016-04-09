package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;


@CucumberOptions(
        features = {
                "src/test/resources/features/app_scan.feature"
        },
        tags = {  }
)
public class AppScanTest extends BaseCucumberTest {

}
