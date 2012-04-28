Description: Access control should be enforced on the server side and should matche the authorisation model for this application

Meta:
@story Authorisation

Scenario: Only authorised users should be able to view resources restricted by roles
Meta:
@Description Resources should only be accessible by the user roles authorised to view them.  Access by unauthorised users could lead to a breach of confidentiality/integrity of the data.
@Reference WASC-02 http://projects.webappsec.org/w/page/13246940/Insufficient%20Authorization
@id access_control_restricted

Given a fresh application
And the login page
And the username <username> 
And the password <password>
When the user logs in
Then they should not be able to access the restricted resource <method>

Examples:
tables/unauthorised.resources.table

Scenario: Un-authenticated users should not be able to view restricted resources
Meta:
@Description Resources that should only be visible to authenticated users should not be accessible if users have not logged in
@Reference WASC-01 http://projects.webappsec.org/w/page/13246939/Insufficient%20Authentication
@id anon_access_control

Given a fresh application
Then they should not be able to access the restricted resource <method>

Examples:
tables/authorised.resources.table