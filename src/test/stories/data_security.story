Confidentiality of sensitive user data

Narrative: 
In order to protect the confidentiality of my sensitive data
As a user
I want to verify that the application does not allow the browser to cache my sensitive data 

Meta: @story data_security

Scenario: Prevent browser caching of sensitive data
Meta: @id cache_control_headers @cwe-525 @browser_only
Given a new browser instance
And the browser is configured to use an intercepting proxy
And the login page
And the username <username>
And the password <password>
When the user logs in
And the proxy logs are cleared
And they access the restricted resource: <method>
And the response that contains the string: <sensitiveData> is recorded
Then the HTTP Cache-control header has the value: no-cache, no-store, must-revalidate
And the HTTP Pragma header has the value: no-cache

Examples:
auto-generated/authorised.resources.table