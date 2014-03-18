
Narrative: 
In order to protect my data
As a user
I want to have a password reset system that does not leak sensitive information 

Meta: @story PasswordRecovery

Scenario: Display a CAPTCHA on the password reset page
Meta: @id recover_captcha @skip
Given a CAPTCHA solver that always fails
When the password recovery feature is requested
Then the CAPTCHA is displayed