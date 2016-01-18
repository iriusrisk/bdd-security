Automated Application Security Scanning

Narrative: 
In order to protect user data
As an application owner
I want to ensure that the application does not suffer from common security vulnerabilities

Meta: @story app_scan

GivenStories: navigate_app.story

Scenario: The application should not contain SQL injection vulnerabilities
Meta: @id scan_sql_injection @cwe-89
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the SQL-Injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file sql_injection.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Cross Site Scripting vulnerabilities
Meta: @id scan_xss @cwe-79
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Cross-Site-Scripting policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file xss.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain path traversal vulnerabilities
Meta: @id scan_path_traversal @cwe-22
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Path-traversal policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file ssi.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain remote file inclusion vulnerabilities
Meta: @id scan_file_inclusion @cwe-98
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Remote-file-inclusion policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file sfi.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side include vulnerabilities
Meta: @id scan_ssi @cwe-97
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Server-side-include policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file ssi.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Server side code injection vulnerabilities
Meta: @id scan_ss_code_injection @cwe-94
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Server-side-code-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file ss_code_injection.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain Remote OS Command injection vulnerabilities
Meta: @id scan_remote_os_injection @cwe-78
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the Remote-os-command-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file os_injection.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain CRLF injection vulnerabilities
Meta: @id scan_crlf_injection @cwe-113
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the crlf-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file crlf_injection.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not contain external redirect vulnerabilities
Meta: @id scan_external_redirect @cwe-601
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the External-redirect policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low 
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file redirect.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not disclose source code
Meta: @id scan_source_disclosure @cwe-541
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the source-code-disclosure policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file source_disclosure.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not be vulnerable to Shell Shock
Meta: @id scan_shell_shock @cwe-78
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the shell-shock policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file shell_shock.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not be vulnerable to LDAP injection
Meta: @id scan_ldap_injection @cwe-90
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the ldap-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file ldap_injection.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not be vulnerable to XPATH injection
Meta: @id scan_xpath_injection @cwe-91
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the xpath-injection policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file xpath_injection.xml
Then no Medium or higher risk vulnerabilities should be present


Scenario: The application should not be vulnerable to Xml External Entity Attacks
Meta: @id scan_xxe @cwe-611
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the xml-external-entity policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file xxe.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not be vulnerable to the Generic Padding Oracle attack
Meta: @id scan_padding_oracle @cwe-209-poodle
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the padding-oracle policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file padding_oracle.xml
Then no Medium or higher risk vulnerabilities should be present

Scenario: The application should not expose insecure HTTP methods
Meta: @id scan_insecure_methods @cwe-200
Given a scanner with all policies disabled
And all existing alerts are deleted
And the URL regular expressions listed in the file: tables/exclude_urls.table are excluded from the scanner
And the insecure-http-methods policy is enabled
And the attack strength is set to High
And the alert threshold is set to Low
When the scanner is run
And the following false positives are removed: tables/zap.false_positives.table
And the XML report is written to the file insecure_methods.xml
Then no Medium or higher risk vulnerabilities should be present
