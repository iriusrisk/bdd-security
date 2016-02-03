@story-configuration
Feature: 
  In order to avoid running tests with a broken configuration
  As a user of this BDD-Security project
  I want to identify configuration errors 
  
  Meta: @story configuration

  @id-config_login_ok
  Scenario Outline: Verify that all configured user accounts can login correctly
    Given a new browser or client instance
    And the username <username>
    And the password <password>
    And the login page
    When the user logs in
    Then the user is logged in

    Examples: 
      | auto-generated/users.table |

  @id-config_wrong_password
  Scenario Outline: Verify that users are not logged in when using an incorrect password
    Given a new browser or client instance
    And the login page
    And the username <username>
    And an incorrect password
    When the user logs in
    Then the user is not logged in

    Examples: 
      | auto-generated/users.table |

  @id-config_is_logged_in
  Scenario: Verify that if users don't login, then they are not logged in (According to the ILogin.isLoggedIn() method)
    Given a new browser or client instance
    And the login page
    Then the user is not logged in
