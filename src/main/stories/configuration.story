Configuration of the test project

Narrative: 
In order to avoid running tests with a broken configuration
As a user of this BDD-Security project
I want to identify configuration errors 

Meta: @story Configuration

Scenario: Verify that all configured user accounts can login correctly
Meta: @id config_login_ok
Given a fresh application
And the username <username> 
And the password <password>
And the login page
When the user logs in
Then the user is logged in
Examples:
users.table


Scenario: Verify that users are not logged in when using an incorrect password
Meta: @id config_wrong_password
Given a fresh application
And the login page
And the username <username>
And an incorrect password
When the user logs in
Then the user is not logged in
Examples:
users.table


Scenario: Verify that if users don't login, then they are not logged in (According to the ILogin.isLoggedIn() method)
Meta: @id config_is_logged_in
Given the login page
Then the user is not logged in

