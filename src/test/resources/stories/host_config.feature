@host_config
Feature: 
  In order to reduce the risk of vulnerabilities introduced through software running on the host
  As a system owner
  I want to ensure that the configuration of the host and network are as expected 

  @open_ports
  Scenario: Only the required ports should be open
    Given the target host name localhost
    When TCP ports from 1 to 65535 are scanned using 100 threads and a timeout of 300 milliseconds
    And the open ports are selected
    Then the ports should be 80,443

