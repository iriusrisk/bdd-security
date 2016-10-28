@app_scan
Feature: Automated Application Security Scanning
  Run automated application level tests against the application using OWASP ZAP

  Background:
    Given a new scanning session
    And a scanner with all policies disabled
    And all existing alerts are deleted
    And the application is navigated
    And the application is spidered

  @iriusrisk-cwe-89
  Scenario: The application should not contain SQL injection vulnerabilities
    And the SQL-Injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/sql_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-79
  Scenario: The application should not contain Cross Site Scripting vulnerabilities
    And the Cross-Site-Scripting policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/xss.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-22
  Scenario: The application should not contain path traversal vulnerabilities
    And the Path-traversal policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-98
  Scenario: The application should not contain remote file inclusion vulnerabilities
    And the Remote-file-inclusion policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/sfi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-97
  Scenario: The application should not contain Server side include vulnerabilities
    And the Server-side-include policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-94
  Scenario: The application should not contain Server side code injection vulnerabilities
    And the Server-side-code-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/ss_code_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-78
  Scenario: The application should not contain Remote OS Command injection vulnerabilities
    And the Remote-os-command-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/os_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-113
  Scenario: The application should not contain CRLF injection vulnerabilities
    And the crlf-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/crlf_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-601
  Scenario: The application should not contain external redirect vulnerabilities
    And the External-redirect policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/redirect.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-541
  Scenario: The application should not disclose source code
    And the source-code-disclosure policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/source_disclosure.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-78
  Scenario: The application should not be vulnerable to Shell Shock
    And the shell-shock policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/shell_shock.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-90
  Scenario: The application should not be vulnerable to LDAP injection
    And the ldap-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/ldap_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-91
  Scenario: The application should not be vulnerable to XPATH injection
    And the xpath-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/xpath_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-611
  Scenario: The application should not be vulnerable to Xml External Entity Attacks
    And the xml-external-entity policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/xxe.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-209-poodle
  Scenario: The application should not be vulnerable to the Generic Padding Oracle attack
    And the padding-oracle policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/padding_oracle.xml
    Then no Medium or higher risk vulnerabilities should be present

  @iriusrisk-cwe-200
  Scenario: The application should not expose insecure HTTP methods
    And the insecure-http-methods policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/insecure_methods.xml
    Then no Medium or higher risk vulnerabilities should be present
