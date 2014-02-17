Description: The automated security scanner should not report serious vulnerabilities in the application
Meta: @story AutomatedScanning

GivenStories: navigate_app.story

Scenario: The application should not contain Cross Site Scripting vulnerabilities
Meta: @id scan_xss
Given a fresh scanner with all policies disabled
And the Cross-Site-Scripting policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain SQL injection vulnerabilities
Meta: @id scan_sql_injection
Given a fresh scanner with all policies disabled
And the SQL-Injection policy is enabled
And the MySQL-SQL-Injection policy is enabled
And the Hypersonic-SQL-Injection policy is enabled
And the Oracle-SQL-Injection policy is enabled
And the PostgreSQL-SQL-Injection policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not contain path traversal vulnerabilities
Meta: @id scan_path_traversal
Given a fresh scanner with all policies disabled
And the Path-traversal policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side include vulnerabilities
Meta: @id scan_ssi
Given a fresh scanner with all policies disabled
And the Server-side-include policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain LDAP injection vulnerabilities
Meta: @id scan_ldap_injection
Given a fresh scanner with all policies disabled
And the LDAP-Injection policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Operating System command injection vulnerabilities
Meta: @id scan_os_injection
Given a fresh scanner with all policies disabled
And the Remote-OS-Command-Injection policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain XPATH injection vulnerabilities
Meta: @id scan_xpath_injection
Given a fresh scanner with all policies disabled
And the XPath-Injection policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain external redirect vulnerabilities
Meta: @id scan_external_redirect
Given a fresh scanner with all policies disabled
And the External-redirect policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain URL Redirector vulnerabilities
Meta: @id scan_url_redirector
Given a fresh scanner with all policies disabled
And the URL-Redirector-Abuse policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain source code disclosure vulnerabilities
Meta: @id scan_source_disclosure
Given a fresh scanner with all policies disabled
And the Source-Code-Disclosure policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no Medium or higher risk vulnerabilities should be present
