@http_headers @broken
Feature: 
  In order to protect my data
  As a user
  I want to verify that HTTP headers adequately protect my data from attackers 

  Background:
    Given a new browser or client instance
    When the following URLs are visited and their HTTP responses recorded
      |baseUrl        |

  @cwe-693-clickjack
  Scenario: Restrict other sites from placing it in an iframe in order to prevent ClickJacking attacks
    Then the X-Frame-Options header is either SAMEORIGIN or DENY

  @cwe-693-x-xss-protection
  Scenario: Enable built in browser protection again Cross Site Scriping
    Then the HTTP X-XSS-Protection header has the value: 1; mode=block

  @cwe-693-strict-transport-security
  Scenario: Force the use of HTTPS for the base secure Url
    Then the Strict-Transport-Security header is set

  @cwe-942-cors_permissive
  Scenario: Restrict HTML5 Cross Domain Requests to only trusted hosts
    Then the Access-Control-Allow-Origin header must not be: *

  @cwe-693-nosniff
  Scenario: Enable anti-MIME sniffing prevention in browsers
    Then the HTTP X-Content-Type-Options header has the value: nosniff
