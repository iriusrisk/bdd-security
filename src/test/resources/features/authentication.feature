@authentication
Feature: Authentication
  Verify that the authentication system is robust

  @iriusrisk-cwe-178-auth
  Scenario: Passwords should be case sensitive
    Given a new browser or client instance
    When the default user logs in
    Then the user is logged in
    When the case of the password is changed
    And the authentication tokens on the client are deleted
    And the login page is displayed
    And the user logs in
    Then the user is not logged in

  @iriusrisk-cwe-295-auth
  Scenario: Present the login form itself over an HTTPS connection
    Given a new browser instance
    And the client/browser is configured to use an intercepting proxy
    And the login page is displayed
    And the HTTP request-response containing the login form
    Then the protocol should be HTTPS

  @iriusrisk-cwe-319-auth
  Scenario: Transmit authentication credentials over HTTPS
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    And the proxy logs are cleared
    When the default user logs in
    And the HTTP request-response containing the default credentials is selected
    Then the protocol should be HTTPS

  @iriusrisk-cwe-525-repost
  Scenario: When authentication credentials are sent to the server, it should respond with a 3xx status code.
    Given a new browser instance
    And the client/browser is configured to use an intercepting proxy
    And the proxy logs are cleared
    When the default user logs in
    And the HTTP request-response containing the default credentials is selected
    Then the response status code should start with 3

  @iriusrisk-cwe-525-autocomplete-form
  Scenario: Disable browser auto-completion on the login form
    Given a new browser instance
    And the login page is displayed
    When the login form is inspected
    Then it should have the autocomplete attribute set to 'off'

  @iriusrisk-cwe-525-autocomplete-password @skip
  Scenario: Disable browser auto-completion on the password field
    Given a new browser instance
    And the login page is displayed
    When the password field is inspected
    Then it should have the autocomplete attribute set to 'off'

  @iriusrisk-auth_lockout @skip
  Scenario: Lock the user account out after 4 incorrect authentication attempts
    Given a new browser or client instance
    And the default username
    And an incorrect password
    And the user logs in from a fresh login page 4 times
    When the default password
    And the user logs in from a fresh login page
    Then the user is not logged in
