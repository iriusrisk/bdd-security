Description: The data transport layer should support security
Meta:
@story Transport

Scenario: The SSL service should use strong cryptographic ciphers and protocols
Meta:
@id transport_ssl_strong

When SSL tests are executed on the secure base Url
Then the service must not be vulnerable to the CRIME attack
And the service must not be vulnerable to the BEAST attack
And the minimum ciphers strength must be 128 bit
And SSL version 2 must not be supported

Scenario: The HTTP headers should protect users from common attacks
Meta:
@id transport_http_headers

Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
When the secure base Url for the application is accessed
And the first HTTP request-response is saved
Then the X-Frame-Options header is either SAMEORIGIN or DENY
And the X-XSS-Protection header contains the value: 1; mode=block
And the Strict-Transport-Security header is set

