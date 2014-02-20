Description: Sensitive data should be adequately protected
Meta: @story DataSecurity


Scenario: Prevent browser caching of sensitive data
Meta: @id cache_control_headers
Given a fresh application
And a browser configured to use an intercepting proxy
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