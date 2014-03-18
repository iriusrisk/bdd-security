package net.continuumsecurity.internal.jbehave;

import net.continuumsecurity.testng.web.NgUtils;
import net.continuumsecurity.web.steps.WebApplicationSteps;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class TestSteps {
    WebApplicationSteps webAppSteps = new WebApplicationSteps();
    protected List<HashMap> authorisedTable;
    protected List<HashMap> unauthorisedTable;


    @BeforeTest
    public void beforeScenario() {
        webAppSteps.createAppAndCredentials();
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.authorisedTable = NgUtils.createListOfMaps(workingDirectory + "/src/main/stories/tables/authorised.resources.table");
        this.unauthorisedTable = NgUtils.createListOfMaps(workingDirectory+"/src/main/stories/tables/unauthorised.resources.table");
    }

    @Test
    public void authorised_users_can_view_restricted_resources() {
            webAppSteps.createApp();
            webAppSteps.enableLoggingDriver();
            webAppSteps.clearProxy();
            webAppSteps.openLoginPage();

            webAppSteps.createApp();
            webAppSteps.openLoginPage();
            webAppSteps.setMethodName("testSearch");
            webAppSteps.checkNoAccessToResource("Results for");
    }
}
