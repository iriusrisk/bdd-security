Description: The data transport layer should be secure

Meta:
@story Transport

Scenario: The SSL service should use strong cryptographic ciphers and protocols
Meta:
@id transport_ssl_strong

Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the HTTP request-response containing the login form
When SSL tests are executed
Then the service must not be vulnerable to the CRIME attack
And the service must not be vulnerable to the BEAST attack
And the minimum ciphers strength must be 128 bit
And SSL version 2 must not be supported