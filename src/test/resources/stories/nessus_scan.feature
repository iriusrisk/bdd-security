@nessus_scan @skip
Feature: Automated Nessus scanning
  In order to identify security vulnerabilities on the hosts
  As a system owner
  I want to scan the hosts for known security vulnerabilities

  Scenario: The host systems should not expose known security vulnerabilities
    Given a nessus API client that accepts all hostnames in SSL certificates
    And a nessus version 6 server at https://localhost:8834
    And the scanning policy named bdd-policy
    And the target host names
      | host       | localhost |
      | ports_open | 80;443    |
    When the scanner is run with scan name bddscan
    And the list of issues is stored
    And the following nessus false positive are removed: plugID 43111 hostname 127.0.0.1
    Then no severity: 2 or higher issues should be present