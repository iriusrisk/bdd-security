@nmap_scan
Feature: Nmap Scan
  Scan the hosts with nmap for known security vulnerabilities

  Scenario: The host systems should only have the required ports open
    Given an nmap instance
    When the scan is executed