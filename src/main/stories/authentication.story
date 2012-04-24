Description: Verify that the authentication system does not suffer from security weaknesses

Meta:
@story Authentication

Scenario: Passwords should be case sensitive
Meta:
@Rationale Case sensitive passords reduce the risk of password guessing and brute force attacks
@id auth_case

When the default user logs in with credentials from: users.table
Then the user is logged in
When the case of the password is changed
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: The login form itself should be served over SSL  
Meta:
@Rationale By serving the page that contains the login form over SSL, the user can verify that it is your site asking for their credentials, and not a phishing attempt.
@id auth_login_form_over_ssl

Given an HTTP logging driver
And clean HTTP logs
And the login page
And the HTTP request-response containing the login form
Then the protocol should be HTTPS

Scenario: Authentication credentials should be transmitted over SSL  
Meta:
@Rationale 
@id auth_https

Given an HTTP logging driver
And clean HTTP logs
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials
Then the protocol should be HTTPS
	
Scenario: When authentication credentials are sent to the server, it should respond with a 3xx status code.  
Meta:
@Rationale If the server responds with a 200 message, then an attacker with access to the same browser as the user could navigate back to the login page and hit reload in the browser.  The browser will then resubmit the login credentials.  This is not possible if the server responds with a 302 status code.
@id auth_return_3xx

Given an HTTP logging driver
And clean HTTP logs
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials
Then the response status code should start with 3

Scenario: The user account should be locked out after 4 incorrect authentication attempts  
Meta:
@Rationale This reduces the risk of password guessing or brute force attacks on a specific user account
@id auth_lockout
@skip

Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the default password is used from: users.table
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: The AUTOCOMPLETE attribute should be disabled on the password field 
Meta:
@Rationale The browser will not give the user the option of storing the password if this attribute is set
@id auth_autocomplete

Given the login page
Then the password field should have the autocomplete directive set to 'off'

