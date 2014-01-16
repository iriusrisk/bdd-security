Description: The SSL service should support strong ciphers and protocols and provide protection against legacy clients
Meta:
@story Ssl

Scenario: The SSL service should not be vulnerable to the CRIME attack
Meta: @id ssl_crime
Given SSL tests have been run on the secure base Url
Then the service must not support SSL compression


Scenario: The SSL service should not be vulnerable to the BEAST attack
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