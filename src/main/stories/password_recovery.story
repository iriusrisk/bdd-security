Narrative: 
In order to protect user data
As a developer responsible for the security of the application
I want to build a password reset system that meets my security requirements 

Meta: @story PasswordRecovery

Scenario: Display a CAPTCHA on the password reset page
Meta: @id recover_captcha @skip
Given a CAPTCHA solver that always fails
When the password recovery feature is requested
Then the CAPTCHA should be present
 