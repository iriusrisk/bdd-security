package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.AutomatedScanningSteps;

public class AutomatedScanningTest {
  protected AutomatedScanningSteps automatedScanningSteps = new AutomatedScanningSteps();

  @BeforeClass
  public void setUp() {
    this.automatedScanningSteps.createScanner();
  }
  
  @BeforeTest
  public void beforeScenario() {
    this.automatedScanningSteps.createScanner();
  }

  @Test
  public void testActiveSecurityScan() throws Exception{
    this.automatedScanningSteps.navigateApp();
    this.automatedScanningSteps.runScanner();
    this.automatedScanningSteps.checkVulnerabilities();
  }

}
