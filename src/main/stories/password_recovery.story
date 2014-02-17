Description: The password recovery feature should be securely implemented
Meta: @story Password Recovery

Scenario: A CAPTCHA should be required for the password recovery feature
Meta: @id recover_captcha
@skip
Given a CAPTCHA solver that always fails
When the password recovery feature is requested
Then the CAPTCHA should be presented
 