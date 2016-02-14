@story-app_scan
Feature: Automated Application Security Scanning
  In order to protect user data
  As a n application owner
  I want to ensure that the application does not suffer from common security vulnerabilities

  Background:
    Given the application has been navigated
    And a scanner with all policies disabled
    And all existing alerts are deleted
    And the following URL regular expressions are excluded from the scanner regex
      | regex      |
      | .*logout.* |

  Scenario: Navigate and spider the application and find vulnerabilities through passive scanning
    Given a new browser or client instance
    And a new scanning session
    And the passive scanner is enabled
    And the page flow described in the method: navigate is run through the proxy
    And the following URL regular expressions are excluded from the spider
      | .*logout.* |
    And the spider is configured for a maximum depth of 10
    And the spider is configured for 1000 maximum children
    And the spider is configured for 10 concurrent threads
    And the following URLs are spidered
      | baseUrl |
    And the spider status reaches 100% complete
    And the following false positives are removed: <url> <parameter> <cweid> <wascid>
      | url     | parameter | cweid     | wascid     |
    When the XML report is written to the file passive.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-89 @id-scan_sql_injection
  Scenario: The application should not contain SQL injection vulnerabilities
    And the SQL-Injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file sql_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-79 @id-scan_xss
  Scenario: The application should not contain Cross Site Scripting vulnerabilities
    And the Cross-Site-Scripting policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file xss.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-22 @id-scan_path_traversal
  Scenario: The application should not contain path traversal vulnerabilities
    And the Path-traversal policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-98 @id-scan_file_inclusion
  Scenario: The application should not contain remote file inclusion vulnerabilities
    And the Remote-file-inclusion policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file sfi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-97 @id-scan_ssi
  Scenario: The application should not contain Server side include vulnerabilities
    And the Server-side-include policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file ssi.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-94 @id-scan_ss_code_injection
  Scenario: The application should not contain Server side code injection vulnerabilities
    And the Server-side-code-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file ss_code_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-78 @id-scan_remote_os_injection
  Scenario: The application should not contain Remote OS Command injection vulnerabilities
    And the Remote-os-command-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file os_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-113 @id-scan_crlf_injection
  Scenario: The application should not contain CRLF injection vulnerabilities
    And the crlf-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file crlf_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-601 @id-scan_external_redirect
  Scenario: The application should not contain external redirect vulnerabilities
    And the External-redirect policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file redirect.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-541 @id-scan_source_disclosure
  Scenario: The application should not disclose source code
    And the source-code-disclosure policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file source_disclosure.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-78 @id-scan_shell_shock
  Scenario: The application should not be vulnerable to Shell Shock
    And the shell-shock policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file shell_shock.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-90 @id-scan_ldap_injection
  Scenario: The application should not be vulnerable to LDAP injection
    And the ldap-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file ldap_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-91 @id-scan_xpath_injection
  Scenario: The application should not be vulnerable to XPATH injection
    And the xpath-injection policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file xpath_injection.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-611 @id-scan_xxe
  Scenario: The application should not be vulnerable to Xml External Entity Attacks
    And the xml-external-entity policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file xxe.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-209-poodle @id-scan_padding_oracle
  Scenario: The application should not be vulnerable to the Generic Padding Oracle attack
    And the padding-oracle policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file padding_oracle.xml
    Then no Medium or higher risk vulnerabilities should be present

  @cwe-200 @id-scan_insecure_methods
  Scenario: The application should not expose insecure HTTP methods
    And the insecure-http-methods policy is enabled
    And the attack strength is set to High
    And the alert threshold is set to Low
    When the scanner is run
    And the following false positives are removed: tables/zap.false_positives.table
    And the XML report is written to the file insecure_methods.xml
    Then no Medium or higher risk vulnerabilities should be present
