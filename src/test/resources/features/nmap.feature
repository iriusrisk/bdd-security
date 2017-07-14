@nmap_scan
Feature: Nmap Scan
  Scan the hosts for known security vulnerabilities and open ports

  @iriusrisk-ssh-service-disabled
  Scenario: the SSH daemon should not be active on any hosts
    Given an nmap instance installed at /usr/local
    And a configured AWS client
    And a list of target hostnames from AWS
    When we scan all ports on each host
    And we extract the open services
    Then ssh must not be one of the services