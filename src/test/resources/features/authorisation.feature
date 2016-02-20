@story-authorisation
Feature: Authorisation and Access Control
  In order to protect my sensitive data
  As a user
  I want to ensure that only the authorised users have access to my data


  @config_authorised_resources
  Scenario Outline: Users can view restricted resources for which they are authorised
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    And the proxy logs are cleared
    And the login page
    And the username <username>
    And the password <password>
    When the user logs in
    And the proxy logs are cleared
    And the HTTP requests and responses on recorded
    And they access the restricted resource: <method>
    Then the string: <sensitiveData> should be present in one of the HTTP responses
    Examples:
      | method              | username | password | sensitiveData               |
      | viewProfileForBob   | bob      | password | Robert                      |
      | viewProfileForBob   | admin    | password | Robert                      |
      | viewProfileForAlice | alice    | password | alice@continuumsecurity.net |
      | viewProfileForAlice | admin    | password | alice@continuumsecurity.net |
      | viewUserList        | admin    | password | User List                   |


  @cwe-639 @access_control_restricted
  Scenario Outline: Users must not be able to view resources for which they are not authorised
    Given the access control map for authorised users has been populated
    And a new browser or client instance
    And the username <username>
    And the password <password>
    And the login page
    When the user logs in
    And the previously recorded HTTP Requests for <method> are replayed using the current session ID
    Then the string: <sensitiveData> should not be present in any of the HTTP responses
    Examples:
      | method              | username | password | sensitiveData               |
      | viewProfileForBob   | alice    | password | Robert                      |
      | viewProfileForAlice | bob      | password | alice@continuumsecurity.net |
      | viewUserList        | alice    | password | User List                   |
      | viewUserList        | bob      | password | User List                   |

  @cwe-306 @anon_access_control
  Scenario Outline: Un-authenticated users should not be able to view restricted resources
    Given the access control map for authorised users has been populated
    And a new browser or client instance
    And the login page
    When the previously recorded HTTP Requests for <method> are replayed using the current session ID
    Then the string: <sensitiveData> should not be present in any of the HTTP responses
    Examples:
      | method              |  sensitiveData                |
      | viewProfileForBob   |   Robert                      |
      | viewProfileForBob   |   Robert                      |
      | viewProfileForAlice |   alice@continuumsecurity.net |
      | viewProfileForAlice |   alice@continuumsecurity.net |
      | viewUserList        |   User List                   |
