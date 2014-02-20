Description: The password reset feature should not leak user data
Meta: @story PasswordRecovery

Scenario: Display a CAPTCHA on the password reset page
Meta: @id recover_captcha
@skip
Given a CAPTCHA solver that always fails
When the password recovery feature is requested
Then the CAPTCHA should be presented
 