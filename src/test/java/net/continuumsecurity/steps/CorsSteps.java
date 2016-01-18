package net.continuumsecurity.steps;

import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ICors;
import net.continuumsecurity.web.Application;

import org.jbehave.core.annotations.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CorsSteps {
    public Application app;
    
    public Application getWebApplication() {
        return app;
    }

    @BeforeStories
    public void beforeStories() {
        Config.getInstance().initialiseTables();
    }

    @When("the path <path> is requested with the HTTP method GET with the 'Origin' header set to <origin>")
    public void corsRequest(@Named("path") String path, @Named("origin") String origin) {
    	((ICors) app).makeCorsRequest(path, origin);
    }
    
    @Then("the returned 'Access-Control-Allow-Origin' header has the value <origin>")
    public void checkAccessControlAllowOriginHeader(@Named("origin") String origin) {
    	String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
    	
    	assertThat("The returned Access-Control-Allow-Origin header equals the Origin", returnedHeader, equalTo(origin));
    }

    @Then("the header 'Access-Control-Allow-Origin' header is not returned")
    public void checkAccessControlAllowOriginHeader() {
    	String returnedHeader = ((ICors) app).getAccessControlAllowOriginHeader();
    	
    	assertThat("The header 'Access-Control-Allow-Origin' header was not returned", returnedHeader, equalTo(null));
    }
}
