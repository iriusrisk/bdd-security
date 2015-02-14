package net.continuumsecurity.internal.jbehave;

import net.continuumsecurity.Utils;
import net.continuumsecurity.steps.WebApplicationSteps;

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
        this.authorisedTable = Utils.createListOfMaps(workingDirectory + "/src/main/stories/tables/authorised.resources.table");
        this.unauthorisedTable = Utils.createListOfMaps(workingDirectory+"/src/main/stories/tables/unauthorised.resources.table");
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
