Description: Access control should be enforced on the server side and should matche the authorisation model for this application

Meta:
@story Authorisation

Scenario: Users can view restricted resources for which they are authorised
Meta:
@id config_authorised_resources

Given a fresh application
And a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the username <username>
And the password <password>
When the user logs in
And the proxy logs are cleared
Then when they access the restricted resource: <method> they should see the string: <sensitiveData>

Examples:
tables/authorised.resources.table

Scenario: Un-authenticated users should not be able to view restricted resources
Meta:
@id anon_access_control

Given the access control map for authorised users has been populated
And a fresh application
And the login page
Then when they access the restricted resource: <method> they should not see the string: <sensitiveData>

Examples:
tables/authorised.resources.table

Scenario: Users must not be able to view resources for which they are not authorised
Meta:
@id access_control_restricted

Given the access control map for authorised users has been populated
And a fresh application
And the login page
And the username <username>
And the password <password>
When the user logs in
Then when they access the restricted resource: <method> they should not see the string: <sensitiveData>

Examples:
tables/unauthorised.resources.table

