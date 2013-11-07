Description: Access control should be enforced on the server side and should matche the authorisation model for this application

Meta:
@story Authorisation

Scenario: Authorised users can view restricted resources
Meta:
@id config_authorised_resources

Given a fresh application
And an HTTP logging driver
And clean HTTP logs
And the login page
And the username <username>
And the password <password>
When the user logs in
And the HTTP logs are cleared
Then they should see the word <verifyString> when accessing the restricted resource <method>

Examples:
tables/authorised.resources.table

Scenario: Un-authenticated users should not be able to view restricted resources
Meta:
@Description Resources that should only be visible to authenticated users should not be accessible if users have not logged in
@Reference WASC-01 http://projects.webappsec.org/w/page/13246939/Insufficient%20Authentication
@id anon_access_control

Given the access control map for authorised users has been populated
And a fresh application
And the login page
Then they should not see the word <verifyString> when accessing the restricted resource <method>

Examples:
tables/authorised.resources.table