package net.continuumsecurity.testng;

import net.continuumsecurity.Utils;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Created by stephen on 12/02/15.
 */
public abstract class BrowserBasedTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    ExamplesTable credentialsTable;

    @BeforeClass
    public void setUp() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.credentialsTable = new ExamplesTable(Utils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/users.table"));
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }
}
