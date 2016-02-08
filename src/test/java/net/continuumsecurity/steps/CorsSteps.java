package net.continuumsecurity.steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.continuumsecurity.behaviour.ICors;
import net.continuumsecurity.web.Application;
import org.junit.BeforeClass;

import javax.inject.Named;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CorsSteps {
    public Application app;
    
    public Application getWebApplication() {
        return app;
    }

    @BeforeClass
    public void beforeStories() {

    }

    @When("the path (.*) is requested with the HTTP method GET with the 'Origin' header set to (.*)")
    public void corsRequest(@Named("path") String path, @Named("origin") String origin) {
    	((ICors) app).makeCorsRequest(path, origin);
    }
    
    @Then("the returned 'Access-Control-Allow-Origin' header has the value (.*)")
    public void checkAccessControlAllowOriginHeader(@Named("origin") String origin) {
    	String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
    	assertThat("The returned Access-Control-Allow-Origin header equals the Origin", returnedHeader, equalTo(origin));
    }

    @Then("the 'Access-Control-Allow-Origin' header is not returned")
    public void checkAccessControlAllowOriginHeader() {
    	String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
    	assertThat("The header 'Access-Control-Allow-Origin' header was not returned", returnedHeader, equalTo(null));
    }
}
