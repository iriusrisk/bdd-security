Meta:
@story Session Management
 
Scenario: When the user logs out then the session should no longer be valid
Meta:
@id sess_logout

Given the default user logs in: users.table
When the user logs out
Then the user is not logged in

Scenario: Sessions should timeout after a period of inactivity
Meta:
@id sess_inactive_timeout
@skip

Given the default user logs in: users.table
When the session is inactive for 30 minutes
Then the user is not logged in

Scenario: The session ID should be changed after authentication
Meta:
@id sess_changed_after_login

Given the login page
And the session cookies
When the default user logs in: users.table
Then the session cookies should be different

Scenario: The session cookie should have the secure flag set
Meta:
@id sess_cookie_secure

Given the default user logs in: users.table
And the session cookies
Then the session cookies should have the secure flag set

Scenario: The session cookie should have the httpOnly flag set
Meta:
@id sess_cookie_httponly

Given the default user logs in: users.table
And the session cookies
Then the session cookies should have the httpOnly flag set