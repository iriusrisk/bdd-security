
Narrative: 

In order to protect my data transmitted over the network
As a user
I want to verify that good SSL practices have been implemented and known weaknesses have been avoided

Meta: @story ssl

Scenario: Disable SSL deflate compression in order to mitigate the risk of the CRIME attack
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


Scenario: Disable SSL version 2
Meta: @id ssl_v2_disabled
Given SSL tests have been run on the secure base Url
Then SSL version 2 must not be supported


Scenario: Enable Perfect forward secrecy
Meta: @id ssl_perfect_forward_secrecy
Given SSL tests have been run on the secure base Url
Then a ECDHE cipher must be enabled
And a DHE cipher must be enabled


Scenario: Support TLSv1.2
Meta: @id ssl_support_tlsv1.2
Given SSL tests have been run on the secure base Url
Then TLSv1.2 should be supported

Scenario: Patch OpenSSL against the Heartbleed vulnerability
Meta: @id ssl_heartbleed
Given SSL tests have been run on the secure base Url
Then the service should be patched against the Heartbleed (CVE-2014-0160) vulnerability