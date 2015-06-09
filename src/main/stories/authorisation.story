Authorisation and Access Control

Narrative: 
In order to protect my sensitive data
As a user
I want to ensure that only the authorised users have access to my data 

Meta: @story authorisation

Scenario: Users can view restricted resources for which they are authorised
Meta: @id config_authorised_resources
Given a new browser instance
And the browser is configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the username <username>
And the password <password>
When the user logs in
And the proxy logs are cleared
And the HTTP requests and responses on recorded
And they access the restricted resource: <method>
Then the string: <sensitiveData> should be present in one of the HTTP responses
Examples:
auto-generated/authorised.resources.table


Scenario: Users must not be able to view resources for which they are not authorised
Meta: @id access_control_restricted @cwe-639
Given the access control map for authorised users has been populated
And a new browser instance
And the username <username>
And the password <password>
And the login page
When the user logs in
And the previously recorded HTTP Requests for <method> are replayed using the current session ID
Then the string: <sensitiveData> should not be present in any of the HTTP responses
Examples:
auto-generated/unauthorised.resources.table


Scenario: Un-authenticated users should not be able to view restricted resources
Meta: @id anon_access_control @cwe-306
Given the access control map for authorised users has been populated
And a new browser instance
And the login page
When the previously recorded HTTP Requests for <method> are replayed using the current session ID
Then the string: <sensitiveData> should not be present in any of the HTTP responses
Examples:
auto-generated/authorised.resources.table