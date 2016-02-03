@navigate-app
Feature: Navigate App

  @pre-navigate
  Scenario Outline: Navigate and spider the application and find vulnerabilities through passive scanning
    Given a new browser or client instance
    And a new scanning session
    And the passive scanner is enabled
    And the page flow described in the method: navigate is run through the proxy
    And the following URL regular expressions are excluded from the spider: exRegex
      | exRegex    |
      | .*logout.* |
    And the spider is configured for a maximum depth of 10
    And the spider is configured for 1000 maximum children
    And the spider is configured for 10 concurrent threads
    And the following URLs are spidered: <url>
    And the spider status reaches 100% complete
    And the following false positives are removed: <urlFP> <parameter> <cweid> <wascid>
      | urlFP   | parameter | cweid     | wascid     |
      | testURL | paramTEST | cweIDTest | wascidTEST |
    And the XML report is written to the file passive.xml
    Then no Medium or higher risk vulnerabilities should be present
    Examples:
      | url     |
      | baseUrl |
