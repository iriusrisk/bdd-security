Description: Verify that appropriate anti-automation controls are in place and working

Meta:
@story Anti-automation

Scenario: The user account should be locked out after 4 incorrect authentication attempts  
Meta:
@Rationale This reduces the risk of password guessing or brute force attacks on a specific user account
@id auto_lockout
@skip

Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the default password is used from: users.table
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: Captcha should be displayed after 3 incorrect authentication attempts
Meta:
@Rationale Reduces the risk of automated brute force or dictionary attacks against the authentication form, but still allows manual password guessing attacks
@id auto_login_captcha

Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 3 times
When the login page is displayed
Then the CAPTCHA request should be present