package net.continuumsecurity.web;

import org.testng.annotations.*;
import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import net.continuumsecurity.web.NgUtils;
import java.lang.System;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class AutomatedScanningTest {
  protected AutomatedScanningSteps automatedScanningSteps = new AutomatedScanningSteps();
  @BeforeClass
  public void setUp() {
    this.automatedScanningSteps.createScanner();
  }
  
  @BeforeTest
  public void beforeScenario() {
    this.automatedScanningSteps.resetScanner();
  }

  @Test
  public void the_application_should_not_contain_vulnerabilities_found_through_passive_scanning() throws Exception{
    this.automatedScanningSteps.setupPassivePolicy();
    this.automatedScanningSteps.navigateApp();
    this.automatedScanningSteps.runScanner("scan_passive");
    this.automatedScanningSteps.checkVulnerabilities();
  }
}
