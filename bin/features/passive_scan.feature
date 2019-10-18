@passive_scan
Feature: Passive Application Security Scanning
  Navigate and spider the application and identify vulnerabilities passively using OWASP ZAP

  Scenario: The application should not contain vulnerabilities identified using passive scanning
    Given a new scanning session
    And the navigation and spider status is reset
    And a scanner with all policies disabled
    And the passive scanner is enabled
    And all existing alerts are deleted
    And the application is navigated
    And the application is spidered
    And the following false positives are removed
      |url                    |parameter          |cweId      |wascId   |
    And the XML report is written to the file build/zap/passive.xml
    Then no Medium or higher risk vulnerabilities should be present