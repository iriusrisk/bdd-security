@nessus_scan
Feature: Nessus Scan
  Scan the hosts for known security vulnerabilities

  Scenario: The host systems should not expose known security vulnerabilities
    Given a nessus API client that accepts all hostnames in SSL certificates
    And a nessus version 6 server at https://localhost:8834
    And the scanning policy named bdd-policy
    And the target host names
      | localhost  |
    When the scanner is run with scan name bddscan
    And the list of issues is stored
    And the following nessus false positive are removed
      |pluginId    |hostname          |
      |73484        | myhost          |

    Then no severity: 2 or higher issues should be present