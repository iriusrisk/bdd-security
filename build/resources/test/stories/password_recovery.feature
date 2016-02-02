@skip @story-password_recovery
Feature: 
  In order to protect my data
  As a user
  I want to have a password reset system that does not leak sensitive information 
  
  Meta: @story password_recovery @skip

  @browser_only @id-recover_captcha @skip
  Scenario: Display a CAPTCHA on the password reset page
    Given a new browser instance
    And a CAPTCHA solver that always fails
    When the password recovery feature is requested
    Then the CAPTCHA is displayed
