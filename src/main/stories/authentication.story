Description: The authentication system should be robust to attack
Meta: @story Authentication

Scenario: Passwords should be case sensitive
Meta: @id auth_case
When the default user logs in with credentials from: users.table
Then the user is logged in
When the case of the password is changed
And the user logs in from a fresh login page
Then the user is not logged in


Scenario: The login form itself should be served over SSL  
Meta: @id auth_login_form_over_ssl
Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the HTTP request-response containing the login form
Then the protocol should be HTTPS


Scenario: The login form must not be available over clear text HTTP
Meta: @id auth_login_form_ssl_enforced
Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the HTTP request-response containing the login form
When the request for the login form is submitted over HTTP
Then the protocol of the current URL should be HTTPS


Scenario: Authentication credentials should be transmitted over SSL  
Meta: @id auth_https
Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials is inspected
Then the protocol should be HTTPS


Scenario: When authentication credentials are sent to the server, it should respond with a 3xx status code.  
Meta: @id auth_return_redirect
Given a browser configured to use an intercepting proxy
And the proxy logs are cleared
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials is inspected
Then the response status code should start with 3


Scenario: The AUTOCOMPLETE attribute should be disabled on the login form
Meta: @id auth_autocomplete_login_form
Given the login page
When the login form is inspected
Then it should have the autocomplete attribute set to 'off'


Scenario: The AUTOCOMPLETE attribute should be disabled on the password field
Meta: @id auth_autocomplete_password
@Skip
Given the login page
When the password field is insepected
Then it should have the autocomplete attribute set to 'off'


Scenario: The user account should be locked out after 4 incorrect authentication attempts  
Meta: @id auth_lockout
Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the default password is used from: users.table
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: Captcha should be displayed after 4 incorrect authentication attempts
Meta: @id auth_login_captcha
@skip
Given the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the login page is displayed
Then the CAPTCHA request should be present
