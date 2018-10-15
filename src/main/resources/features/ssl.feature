@ssl
Feature: SSL
  Ensure that the SSL configuration of the service is robust

  Background: Run the SSLyze command only once for all features
    When the SSLyze command is run against the application

  @iriusrisk-ssl_crime
  Scenario: Disable SSL deflate compression in order to mitigate the risk of the CRIME attack
    Then the output must contain the text "Compression disabled"

  @iriusrisk-ssl_client_renegotiations
  Scenario: Disable client renegotiations
    Then the output must contain a line that matches .*Client-initiated Renegotiation?:\s+OK - Rejected.*

  @iriusrisk-ssl_heartbleed
  Scenario: Patch OpenSSL against the Heartbleed vulnerability
    Then the output must contain a line that matches .*Not vulnerable to Heartbleed.*

  @iriusrisk-ssl_strong_cipher
  Scenario: The minimum cipher strength should meet requirements
    Then the minimum key size must be 128 bits

  @iriusrisk-ssl_disabled_protocols
  Scenario: Disable weak SSL protocols due to numerous cryptographic weaknesses
    Then the following protocols must not be supported
      |SSLV1      |
      |SSLV2      |
      |SSLV3      |

  @iriusrisk-ssl_support_strong_protocols
  Scenario: Support TLSv1.2
    Then the following protocols must be supported
      |TLSV1_2  |

  @iriusrisk-ssl_perfect_forward_secrecy
  Scenario: Enable Perfect forward secrecy
    Then any of the following ciphers must be supported
      |TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384  |
      |TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384  |
      |TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256  |
      |TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256  |
      |ECDHE-RSA-AES128-SHA                   |
      |ECDHE-RSA-AES256-SHA                   |
      |DHE-DSS-CAMELLIA128-SHA                |
      |DHE-DSS-CAMELLIA256-SHA                |
      |DHE-RSA-CAMELLIA128-SHA                |
      |DHE-RSA-CAMELLIA256-SHA                |
      |ECDHE-ECDSA-CAMELLIA128-SHA256         |
      |ECDHE-ECDSA-CAMELLIA256-SHA384         |
      |ECDH-ECDSA-CAMELLIA128-SHA256          |
      |ECDH-ECDSA-CAMELLIA256-SHA384          |
      |ECDHE-RSA-CAMELLIA128-SHA256           |
      |ECDHE-RSA-CAMELLIA256-SHA384           |
      |ECDH-RSA-CAMELLIA128-SHA256            |
      |ECDH-RSA-CAMELLIA256-SHA384            |


