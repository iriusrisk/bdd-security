package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;

@CucumberOptions(
        features = {
                "src/test/resources/features/navigate_app.feature"
        }
)
public class NavigateAppTestCase extends BaseCucumberTestCase {

}
