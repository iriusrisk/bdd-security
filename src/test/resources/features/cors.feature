@cors @skip
Feature: Cross Origin Resource Sharing
  Verify that the application does not allow the browser to perform requests outside of the allowed origins

  @iriusriskcwe-942-cors_allowed
  Scenario Outline: Permit allowed origins to make CORS requests
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    When the path <path> is requested with the HTTP method GET with the 'Origin' header set to <origin>
    Then the returned 'Access-Control-Allow-Origin' header has the value <origin>
    Examples:
     |path                         |origin               |


  @iriusrisk-cwe-942-cors_disallowed
  Scenario Outline: Forbid disallowed origins from making CORS requests
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    When the path <path> is requested with the HTTP method GET with the 'Origin' header set to <origin>
    Then the 'Access-Control-Allow-Origin' header is not returned
    Examples:
      |path                         |origin               |

