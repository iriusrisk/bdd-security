@authorisation
Feature: Authorisation and Access Control
  Verify that the access control model is enforced so that only the authorised users have access to their own data

  @iriusrisk-authorised_resources
  Scenario Outline: Users can view restricted resources for which they are authorised
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    And the proxy logs are cleared
    And the login page
    And the username <username> is used
    And the password <password> is used
    When the user logs in
    And the proxy logs are cleared
    And the HTTP requests and responses are recorded
    And they access the restricted resource: <method>
    Then the string: <sensitiveData> should be present in one of the HTTP responses
    Examples:
      | method              | username | password | sensitiveData               |
      | viewBobsProfile     | bob      | password | Robert                      |
      | viewAlicesProfile   | alice    | password | alice@continuumsecurity.net |
      | viewAllUsers        | admin    | password | User List                   |


  @iriusrisk-cwe-639
  Scenario Outline: Users must not be able to view resources for which they are not authorised
    Given the access control map for authorised users has been populated
    And a new browser or client instance
    And the username <username> is used
    And the password <password> is used
    And the login page
    When the user logs in
    And the previously recorded HTTP Requests for <method> are replayed using the current session ID
    Then the string: <sensitiveData> should not be present in any of the HTTP responses
    Examples:
      | method              | username | password | sensitiveData               |
      | viewBobsProfile     | alice    | password | Robert                      |
      | viewAlicesProfile   | bob      | password | alice@continuumsecurity.net |
      | viewAllUsers        | alice    | password | User List                   |
      | viewAllUsers        | bob      | password | User List                   |

  @iriusrisk-cwe-306
  Scenario Outline: Un-authenticated users should not be able to view restricted resources
    Given the access control map for authorised users has been populated
    And a new browser or client instance
    And the login page
    When the previously recorded HTTP Requests for <method> are replayed using the current session ID
    Then the string: <sensitiveData> should not be present in any of the HTTP responses
    Examples:
      | method              |  sensitiveData                |
      | viewBobsProfile     |   Robert                      |
      | viewAlicesProfile   |   alice@continuumsecurity.net |
      | viewAllUsers        |   User List                   |
