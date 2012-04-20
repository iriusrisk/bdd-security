Meta:
@story Session Management
 
Scenario: When the user logs out then the session should no longer be valid
Meta:
@id sess_logout

When the default user logs in with credentials from: users.table
And the user logs out
Then the user is not logged in

Scenario: Sessions should timeout after a period of inactivity
Meta:
@id sess_inactive_timeout
@skip

When the default user logs in with credentials from: users.table
And the session is inactive for 30 minutes
Then the user is not logged in

Scenario: The session cookie should have the secure flag set
Meta:
@id sess_cookie_secure

Given the default user logs in with credentials from: users.table
And the session cookies
Then the session cookies should have the secure flag set

Scenario: The session cookie should have the httpOnly flag set
Meta:
@id sess_cookie_httponly

Given the default user logs in with credentials from: users.table
And the session cookies
Then the session cookies should have the httpOnly flag set