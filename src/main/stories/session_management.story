
Description: Session management functions should be security implemented.
Meta:
@story Session Management

Scenario: The session ID should be changed after authentication
Meta:
@Description If the session cookies are not changed after authentication, then an attacker with access to the victim's browser could navigate to the login form and note the session IDs, then wait for a victim to login using that browser, and then impersonate them on the site using the noted IDs.
@Reference WASC-37 http://projects.webappsec.org/w/page/13246960/Session%20Fixation
@id session_fixation

Given the login page
And the session cookies
When the default user logs in with credentials from: users.table
Then the session cookies after authentication should be different from those issued before

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
@id sess_inactive_timeout
@skip

When the default user logs in with credentials from: users.table
And the session is inactive for 30 minutes
Then the user is not logged in

Scenario: The session cookie should have the secure flag set
Meta:
@id session_cookie_secure

Given the default user logs in with credentials from: users.table
And the session cookies
Then the session cookies should have the secure flag set

Scenario: The session cookie should have the httpOnly flag set
Meta:
@id session_cookie_httponly

Given an HTTP logging driver
And clean HTTP logs
And the default user logs in with credentials from: users.table
Then the session cookies should have the httpOnly flag set