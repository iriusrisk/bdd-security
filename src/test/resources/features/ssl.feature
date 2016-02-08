@ssl
Feature:
  In order to protect my data transmitted over the network
  As a user
  I want to verify that good SSL practices have been implemented and known weaknesses have been avoided

  @ssl
  Scenario Outline: the SSL configuration meets our requirements for security
    When the SSLyze command is run against the <host> on <port>
    Then the output must contain the text "Compression disabled"
    And the output must contain a line that matches .*Client-initiated Renegotiations:\s+OK - Rejected.*
    And the output must contain a line that matches .*Not vulnerable to Heartbleed.*
    And the minimum key size must be 128 bits
    And the following protocols must not be supported
      | SSLV1    |
      | SSLV2    |
      | SSLV3    |
    And the following protocols must be supported
      | TLSV1_2  |
    And any of the following ciphers must be supported
      | ECDHE-RSA-AES128-SHA           |
      | ECDHE-RSA-AES256-SHA           |
      | DHE-DSS-CAMELLIA128-SHA        |
      | DHE-DSS-CAMELLIA256-SHA        |
      | DHE-RSA-CAMELLIA128-SHA        |
      | DHE-RSA-CAMELLIA256-SHA        |
      | ECDHE-ECDSA-CAMELLIA128-SHA256 |
      | ECDHE-ECDSA-CAMELLIA256-SHA384 |
      | ECDH-ECDSA-CAMELLIA128-SHA256  |
      | ECDH-ECDSA-CAMELLIA256-SHA384  |
      | ECDHE-RSA-CAMELLIA128-SHA256   |
      | ECDHE-RSA-CAMELLIA256-SHA384   |
      | ECDH-RSA-CAMELLIA128-SHA256    |
      | ECDH-RSA-CAMELLIA256-SHA384    |
    Examples:
      |host             |port   |
      |www.google.com   |443    |