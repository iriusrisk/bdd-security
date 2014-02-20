Description: Set HTTP headers that enable built in browser security controls, and prevent common attacks
Meta: @story http_headers

Scenario: Restrict other sites from placing it in an iframe in order to prevent ClickJacking attacks
Meta: @id headers_xframe_options
When the secure base Url is accessed and the HTTP response recorded
Then the X-Frame-Options header is either SAMEORIGIN or DENY

Scenario: Enable built in browser protection again Cross Site Scriping
Meta: @id headers_xss_protection
When the secure base Url is accessed and the HTTP response recorded
Then the HTTP X-XSS-Protection header has the value: 1; mode=block

Scenario: Force the use of HTTPS for the base secure Url
Meta: @id headers_sts
When the secure base Url is accessed and the HTTP response recorded
Then the Strict-Transport-Security header is set

Scenario: Restrict HTML5 Cross Domain Requests to only trusted hosts
Meta: @id headers_cors
When the secure base Url is accessed and the HTTP response recorded
Then the Access-Control-Allow-Origin header must not be: *

Scenario: Enable anti-MIME sniffing prevention in browsers
Meta: @id headers_nosniff
When the secure base Url is accessed and the HTTP response recorded
Then the HTTP X-Content-Type-Options header has the value: nosniff