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
And RC4 ciphers must not be supported

Scenario: The HTTP headers should protect users from common attacks
Meta:
@id transport_http_headers

Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
When the secure base Url for the application is accessed
And the first HTTP request-response is recorded
Then the X-Frame-Options header is either SAMEORIGIN or DENY
And the HTTP X-XSS-Protection header has the value: 1; mode=block
And the Strict-Transport-Security header is set
And the Access-Control-Allow-Origin header must not be: *
And the HTTP X-Content-Type-Options header has the value: nosniff


Scenario: Pages that contain sensitive data should have the cache-control headers set
Meta:
@id cache_control_http_headers

Given a fresh application
And a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the username <username>
And the password <password>
When the user logs in
And the proxy logs are cleared
And they access the restricted resource: <method> and the response that contains the string: <sensitiveData> is recorded
Then the HTTP Cache-control header has the value: no-cache, no-store, must-revalidate
And the HTTP Pragma header has the value: no-cache

Examples:
tables/authorised.resources.table