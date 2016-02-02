@story-http_headers
Feature: 
  In order to protect my data
  As a user
  I want to verify that HTTP headers adequately protect my data from attackers 
  
  Meta: @story http_headers

  @browser_only @cwe-693-clickjack @id-headers_xframe_options
  Scenario: Restrict other sites from placing it in an iframe in order to prevent ClickJacking attacks
    Given a new browser or client instance
    When the secure base Url is accessed and the HTTP response recorded
    Then the X-Frame-Options header is either SAMEORIGIN or DENY

  @browser_only @cwe-693-x-xss-protection @id-headers_xss_protection
  Scenario: Enable built in browser protection again Cross Site Scriping
    Given a new browser or client instance
    When the secure base Url is accessed and the HTTP response recorded
    Then the HTTP X-XSS-Protection header has the value: 1; mode=block

  @browser_only @cwe-693-strict-transport-security @id-headers_sts
  Scenario: Force the use of HTTPS for the base secure Url
    Given a new browser or client instance
    When the secure base Url is accessed and the HTTP response recorded
    Then the Strict-Transport-Security header is set

  @browser_only @cwe-942-cors @id-headers_cors
  Scenario: Restrict HTML5 Cross Domain Requests to only trusted hosts
    Given a new browser or client instance
    When the secure base Url is accessed and the HTTP response recorded
    Then the Access-Control-Allow-Origin header must not be: *

  @browser_only @cwe-693-nosniff @id-headers_nosniff
  Scenario: Enable anti-MIME sniffing prevention in browsers
    Given a new browser or client instance
    When the secure base Url is accessed and the HTTP response recorded
    Then the HTTP X-Content-Type-Options header has the value: nosniff
