package net.continuumsecurity.junit;

import cucumber.api.CucumberOptions;

@CucumberOptions(
        features = {
                "src/test/resources/features/http_headers.feature"
        },
        tags = {}
)
public class HttpHeadersTest extends BaseCucumberTest {
}
