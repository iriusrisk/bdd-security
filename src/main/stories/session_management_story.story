
Description: Session management functions should be security implemented.
Meta:
@story SessionManagement

Scenario: The session ID should be changed after authentication
Meta:
@Reference WASC-37 http://projects.webappsec.org/w/page/13246960/Session%20Fixation
@id session_fixation

Given the login page
And the value of the session cookie is noted
When the default user logs in with credentials from: users.table
Then the value of the session cookie issued after authentication should be different from that of the previously noted session ID

Scenario: When the user logs out then the session should no longer be valid
Meta:
@Reference WASC-47 http://projects.webappsec.org/w/page/13246944/Insufficient%20Session%20Expiration
@id session_logout

When the default user logs in with credentials from: users.table
And the user logs out
Then the user is not logged in

Scenario: Sessions should timeout after a period of inactivity
Meta:
@Reference WASC-47 http://projects.webappsec.org/w/page/13246944/Insufficient%20Session%20Expiration
@id session_inactive_timeout
@skip

When the default user logs in with credentials from: users.table
And the session is inactive for 15 minutes
Then the user is not logged in

Scenario: The session cookie should have the secure flag set
Meta:
@id session_cookie_secure

Given the default user logs in with credentials from: users.table
And the value of the session cookie is noted
Then the session cookie should have the secure flag set

Scenario: The session cookie should have the httpOnly flag set
Meta:
@id session_cookie_httponly

Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the default user logs in with credentials from: users.table
Then the session cookie should have the httpOnly flag set