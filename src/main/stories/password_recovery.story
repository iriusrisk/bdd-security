Description: The password recovery feature should be securely implemented

Meta:
@story Password Recovery

Scenario: A CAPTCHA should be required for the password recovery feature
Meta:
@Description The password recover feature can often be used by automated attack tools to discover registered usernames or email addresses.  By requiring that the user solve a CAPTCHA on this form, the risk of successful automated attack is reduced.
@Reference WASC-21 http://projects.webappsec.org/w/page/13246938/Insufficient%20Anti-automation
@id recover_captcha

Given a CAPTCHA solver that always fails
When the password recovery feature is requested
Then the CAPTCHA should be presented again 
 