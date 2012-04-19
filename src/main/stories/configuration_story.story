Meta:
@story Configuration

Scenario: Verify that all users can login correctly 
Meta:
@id config_login_ok

Given a fresh application
And the login page
And the username <username> 
And the password <password>
When the user logs in
Then the user is logged in

Examples:
users.table

Scenario: Verify that users are not logged in when using an incorrect password
Meta:
@id config_wrong_password

Given a fresh application
And the login page
And the username <username>
And an incorrect password
When the user logs in
Then the user is not logged in

Examples:
users.table

Scenario: Verify that if users don't login, then they are not logged in (According to the ILogin.isLoggedIn(Role) method)
Meta:
@id config_is_logged_in

Given the login page
Then the user is not logged in

Scenario: Verify that the methods tagged with @SecurityScan can be navigated without errors
Meta:
@id config_navigate_all

Given the login page
When the application is navigated
Then no exceptions are thrown

Scenario: Verify that authorised users can view restricted resources
Meta:
@id config_authorisation

Given a fresh application
And the login page
And the username <username> 
And the password <password>
When the user logs in
Then they should be able to access the restricted resource <method>

Examples:
authorised.resources.table