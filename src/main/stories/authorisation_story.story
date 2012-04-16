Meta:
@story Authorisation

Scenario: Verify that only authorised users can view restricted resources
Meta:
@id access_control_restricted

Given a fresh application
And the login page
And the username <username> 
And the password <password>
When the user logs in
Then they should not be able to access the restricted resource <method>

Examples:
unauthorised.resources.table