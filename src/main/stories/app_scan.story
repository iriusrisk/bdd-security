Automated Application Security Scanning

Narrative: 
In order to protect my data
As a user
I want to ensure that the application does not suffer from common security vulnerabilities

Meta: @story app_scan


Scenario: The application should not contain SQL injection vulnerabilities
Meta: @id scan_sql_injection
GivenStories: navigate_app.story
Given a scanner with all policies disabled
And the SQL-Injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Medium
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Cross Site Scripting vulnerabilities
Meta: @id scan_xss
GivenStories: navigate_app.story
Given a scanner with all policies disabled
And the Cross-Site-Scripting policy is enabled
And the attack strength is set to High
And the alert threshold is set to Medium
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain path traversal vulnerabilities
Meta: @id scan_path_traversal
GivenStories: navigate_app.story
Given a scanner with all policies disabled
And the Path-traversal policy is enabled
And the attack strength is set to High
And the alert threshold is set to Medium
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side include vulnerabilities
Meta: @id scan_ssi
GivenStories: navigate_app.story
Given a scanner with all policies disabled
And the Server-side-include policy is enabled
And the attack strength is set to High
And the alert threshold is set to Medium
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain external redirect vulnerabilities
Meta: @id scan_external_redirect
GivenStories: navigate_app.story
Given a scanner with all policies disabled
And the External-redirect policy is enabled
And the attack strength is set to High
And the alert threshold is set to Medium
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present