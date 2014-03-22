Authorisation and Access Control

Narrative: 
In order to protect my sensitive data
As a user
I want to ensure that only the authorised users have access to my data 

Meta: @story Authorisation

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
Then when they access the restricted resource: <method> they should see the string: <sensitiveData>
Examples:
tables/authorised.resources.table


Scenario: Users must not be able to view resources for which they are not authorised
Meta: @id access_control_restricted
Given the access control map for authorised users has been populated
And a new browser instance
And the username <username>
And the password <password>
And the login page
When the user logs in
And they access the restricted resource: <method>
Then they should not see the string: <sensitiveData>
Examples:
tables/unauthorised.resources.table


Scenario: Un-authenticated users should not be able to view restricted resources
Meta: @id anon_access_control
Given the access control map for authorised users has been populated
And a new browser instance
And the login page
When they access the restricted resource: <method>
Then they should not see the string: <sensitiveData>
Examples:
tables/authorised.resources.table