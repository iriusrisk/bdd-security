Description: The SSL service should support strong ciphers and protocols and provide protection against legacy clients
Meta: @story Ssl

Scenario: The SSL service should not support deflate compression in order to mitigate the risk of the CRIME attack
Meta: @id ssl_crime
Given SSL tests have been run on the secure base Url
Then the service must not support SSL compression


Scenario: The SSL service should either support TLSv1.1+ or prefer RC4 ciphers over CBC in order to mitigate the risk of the BEAST attack
Meta: @id ssl_beast
Given SSL tests have been run on the secure base Url
Then the service must not be vulnerable to the BEAST attack


Scenario: The minimum cipher strength should be at least 128 bit
Meta: @id ssl_strong_cipher
Given SSL tests have been run on the secure base Url
Then the minimum ciphers strength must be 128 bit


Scenario: SSL version 2 should be disabled
Meta: @id ssl_v2_disabled
Given SSL tests have been run on the secure base Url
Then SSL version 2 must not be supported


Scenario: RC4 ciphers should be disabled
Meta: @id ssl_rc4_disabled
Given SSL tests have been run on the secure base Url
Then RC4 ciphers must not be supported


Scenario: Perfect forward secrecy should be enabled
Meta: @id ssl_perfect_forward_secrecy
Given SSL tests have been run on the secure base Url
Then a ECDHE cipher must be enabled
And a DHE cipher must be enabled


Scenario: TLSv1.2 should be supported
Meta: @id ssl_support_tlsv1.2
Given SSL tests have been run on the secure base Url
Then TLSv1.2 should be supported