Automated Application Security Scanning

Narrative: 
In order to protect user data
As an application owner
I want to ensure that the application does not suffer from common security vulnerabilities

Meta: @story app_scan

GivenStories: navigate_app.story

Scenario: The application should not contain SQL injection vulnerabilities
Meta: @id scan_sql_injection
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the SQL-Injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Cross Site Scripting vulnerabilities
Meta: @id scan_xss
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Cross-Site-Scripting policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain path traversal vulnerabilities
Meta: @id scan_path_traversal
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Path-traversal policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain remote file inclusion vulnerabilities
Meta: @id scan_file_inclusion
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Remote-file-inclusion policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side include vulnerabilities
Meta: @id scan_ssi
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Server-side-include policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side code injection vulnerabilities
Meta: @id scan_ss_code_injection
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Server-side-code-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Remote OS Command injection vulnerabilities
Meta: @id scan_remote_os_injection
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Remote-os-command-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain CRLF injection vulnerabilities
Meta: @id scan_crlf_injection
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the crlf-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain external redirect vulnerabilities
Meta: @id scan_external_redirect
Given a scanner with all policies disabled
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the External-redirect policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed
|url                           |parameter           |cweid  |
Then no Medium or higher risk vulnerabilities should be present
