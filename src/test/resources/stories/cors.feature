@skip @cors
Feature: 
  In order to reduce the risk of Cross Site Request Forgery
  As a system owner
  I want to verify that the application does not allow the browser to perform requests outside of the allowed origins

  @cwe-942-cors @cors_successful_from_allowed_origins
  Scenario: Permit allowed origins to make CORS requests
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    #todo in the below step you should add the expected PATH and ORIGIN. apparently allowed.cors.requests.table was empty.
    #todo So it looks like this test was always running with empty data input.
    When the path <path> is requested with the HTTP method GET with the 'Origin' header set to <origin>
    Then the returned 'Access-Control-Allow-Origin' header has the value <origin>


  @cwe-942-cors @cors_unsuccessful_from_disallowed_origins
  Scenario: Forbid disallowed origins from making CORS requests
    Given a new browser or client instance
    And the client/browser is configured to use an intercepting proxy
    #todo see my comment from the previous scenario
    When the path <path> is requested with the HTTP method GET with the 'Origin' header set to <origin>
    Then the header 'Access-Control-Allow-Origin' header is not returned

