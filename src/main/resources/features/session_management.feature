@session_management
Feature: Session Management
  Verify that there are no weaknesses in the session management implementation

  @iriusrisk-cwe-664-fixation
  Scenario: Issue a new session ID after authentication
    Given a new browser or client instance
    And the login page
    And the value of the session ID is noted
    When the default user logs in
    And the user is logged in
    Then the value of the session cookie issued after authentication should be different from that of the previously noted session ID

  @iriusrisk-cwe-613-logout
  Scenario: Invalidate the session when the user logs out
    Given a new browser or client instance
    When the default user logs in
    Then the user is logged in
    When the user logs out
    Then the user is not logged in

  @iriusrisk-cwe-613 @skip
  Scenario: Invalidate the session after a period of inactivity
    Given a new browser or client instance
    When the default user logs in
    Then the user is logged in
    When the session is inactive for 15 minutes
    Then the user is not logged in

  @iriusrisk-cwe-614
  Scenario: Set the 'secure' flag on the session cookie
    Given a new browser or client instance
    When the default user logs in
    And the user is logged in
    Then the session cookie should have the secure flag set

  @iriusrisk-wasc-13
  Scenario: Set the 'httpOnly' flag on the session cookie
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    When the default user logs in
    And the user is logged in
    Then the session cookie should have the httpOnly flag set
