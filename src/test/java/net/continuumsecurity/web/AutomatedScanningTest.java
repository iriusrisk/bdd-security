package net.continuumsecurity.web;

import net.continuumsecurity.Constants;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AutomatedScanningTest {
    protected AutomatedScanningSteps automatedScanningSteps = new AutomatedScanningSteps();
    ExamplesTable falsePositives;


    @BeforeClass
    public void beforeStory() throws Exception {
        String workingDirectory = System.getProperty("user.dir");
        falsePositives = new ExamplesTable(NgUtils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/tables/false_positives.table"));
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @Test
    public void test_xss() throws Exception {
        automatedScanningSteps.createNewScanSession();
        automatedScanningSteps.navigateApp();
        automatedScanningSteps.enablePolicy(Constants.XSSPOLICY);
        automatedScanningSteps.runScanner();
        automatedScanningSteps.removeFalsePositives(falsePositives);
        automatedScanningSteps.checkVulnerabilities("High");
        automatedScanningSteps.checkVulnerabilities("Medium");
    }

}
