@host_config @skip
Feature: 
  In order to reduce the risk of vulnerabilities introduced through software running on the host
  As a system owner
  I want to ensure that the configuration of the host and network are as expected 

  @open_ports
  Scenario Outline: Only the required ports should be open
    Given the target host name <host>
    When TCP ports from <startPort> to <endPort> are scanned using <threads> threads and a timeout of <timeout> milliseconds
    And the <state> ports are selected
    Then the ports should be <ports>
    Examples:
      |host         |startPort  |endPort  |threads |timeout   |state      |ports      |
      |localhost    |1          |65535    |100     |500       |open       |80,443     |
