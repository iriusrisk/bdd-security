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
    this.automatedScanningSteps.resetScanner();
  }

  @Test
  public void the_application_should_not_contain_vulnerabilities_found_through_passive_scanning() throws Exception{
    this.automatedScanningSteps.setupPassivePolicy();
    this.automatedScanningSteps.navigateApp();
    this.automatedScanningSteps.runScanner("scan_passive");
    this.automatedScanningSteps.checkVulnerabilities();
  }

  @Test
  public void there_should_be_no_SQL_injection_vulnerabilities_present() throws Exception{
    this.automatedScanningSteps.setupSQLinjectionPolicy();
    this.automatedScanningSteps.enableAllInjectionPoints();
    this.automatedScanningSteps.runScanner("scan_sql");
    this.automatedScanningSteps.checkVulnerabilities();
  }

  @Test
  public void there_should_be_no_cross_site_scripting_vulnerabilities_present() throws Exception{
    this.automatedScanningSteps.setupXSSPolicy();
    this.automatedScanningSteps.enableAllInjectionPoints();
    this.automatedScanningSteps.runScanner("scan_xss");
    this.automatedScanningSteps.checkVulnerabilities();
  }

  @Test
  public void there_should_be_no_command_injection_vulnerabilities_present() throws Exception{
    this.automatedScanningSteps.setupCommandInjectionPolicy();
    this.automatedScanningSteps.enableAllInjectionPoints();
    this.automatedScanningSteps.runScanner("scan_cmd");
    this.automatedScanningSteps.checkVulnerabilities();
  }

  @Test
  public void there_should_be_no_LDAP_Injection_vulnerabilities_present() throws Exception{
    this.automatedScanningSteps.setupLDAPinjectionPolicy();
    this.automatedScanningSteps.enableAllInjectionPoints();
    this.automatedScanningSteps.runScanner("scan_ldap");
    this.automatedScanningSteps.checkVulnerabilities();
  }
  
  @Test
  public void there_should_be_no_XML_SOAP_Injection_vulnerabilities_present() throws Exception{
   this.automatedScanningSteps.setupXMLinjectionPolicy();
   this.automatedScanningSteps.enableAllInjectionPoints();
   this.automatedScanningSteps.runScanner("scan_xml");
   this.automatedScanningSteps.checkVulnerabilities();
  }

  @Test
  public void there_should_be_no_Miscellaneous_header_and_server_vulnerabilities_present() throws Exception{
    this.automatedScanningSteps.setupMiscPolicy();
    this.automatedScanningSteps.enableAllInjectionPoints();
    this.automatedScanningSteps.runScanner("scan_misc");
    this.automatedScanningSteps.checkVulnerabilities();
  }
}
