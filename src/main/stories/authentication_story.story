Meta:
@story Authentication

Scenario: Passwords should be case sensitive
Meta:
@id auth_case

Given the default user logs in: users.table
Then the user is logged in
When the case of the password is changed
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: The user account should be locked out after 4 incorrect authentication attempts
Meta:
@id auth_lockout

Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the default password is used from: users.table
And the user logs in from a fresh login page
Then the user is not logged in


Scenario: Login should be secure against SQL injection bypass attacks in the password field
Meta:
@id auth_sql_bypass

Given the login page
And the default username from: users.table
When the password is changed to values from <value>
And the user logs in
Then the user is not logged in

Examples:
sqlinjection.strings.table

Scenario: Login should be secure against SQL injection bypass attacks in the username field
Meta:
@id auth_sql_bypass2

Given the login page
And the default username from: users.table
When an SQL injection <value> is appended to the username
And the user logs in
Then the user is not logged in

Examples:
sqlinjection.strings.table 

Scenario: The login form itself should be served over SSL to reduce the risk of Phishing attacks
Meta:
@id auth_login_form_over_ssl

Given an HTTP logging driver
And clean HTTP logs
And the login page
And the HTTP request containing the password form
Then the protocol should be HTTPS

Scenario: The authentication credentials should be sent over SSL
Meta:
@id auth_over_ssl

Given an HTTP logging driver
And clean HTTP logs
And the default user logs in: users.table
And the HTTP request-response containing the default credentials
Then the protocol should be HTTPS

Scenario: When authentication credentials are sent to the server, it should respond with a 3xx status code
Meta:
@id auth_return_3xx

Given an HTTP logging driver
And clean HTTP logs
And the default user logs in: users.table
And the HTTP request-response containing the default credentials
Then the response status code should start with 3
