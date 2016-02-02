@skip @story-nessus_scan
Feature: Automated Nessus scanning
  In order to identify security vulnerabilities on the hosts
  As a system owner
  I want to scan the hosts for known security vulnerabilities

  Scenario Outline: The host systems should not expose known security vulnerabilities
    Given a nessus API client that accepts all hostnames in SSL certificates
    And a nessus version 6 server at https://localhost:8834
    And the scanning policy named bdd-policy
    And the target host names
      | host       | localhost |
      | ports_open | 80;443    |
    When the scanner is run with scan name bddscan
    And the list of issues is stored
    And the following nessus false positive are removed: <PluginID> <Hostname>
    Then no severity: 2 or higher issues should be present
    #Basically if you add a new row in this table, this scenario will run twice (one run for every row)
    Examples:
      | PluginID | Hostname  |
      | 43111    | 127.0.0.1 |
