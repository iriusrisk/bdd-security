Narrative: 
In order to protect the confidentiality of sensitive user data
As a developer responsible for the security of the application
I want to ensure that appropriate controls are in place to prevent disclosure of this data both on the client and server side 

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