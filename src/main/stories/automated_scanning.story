Description: The automated security scanner should not report serious vulnerabilities in the application
Meta: @story AutomatedScanning


Scenario: The application should not contain vulnerabilities found through an active scanner
Meta: @id automated_scan

Given a new scanning session
And all scan policies are disabled
And the passive scanner is enabled
And the scannable methods of the application are navigated through the proxy
And the Cross-Site-Scripting policy is enabled
And the SQL-Injection policy is enabled
And the MySQL-SQL-Injection policy is enabled
And the Hypersonic-SQL-Injection policy is enabled
And the Oracle-SQL-Injection policy is enabled
And the PostgreSQL-SQL-Injection policy is enabled
And the Path-traversal policy is enabled
And the Source-Code-Disclosure policy is enabled
And the URL-Redirector-Abuse policy is enabled
And the Server-side-include policy is enabled
And the LDAP-Injection policy is enabled
And the Server-Side-Code-Injection-Plugin policy is enabled
And the Remote-OS-Command-Injection-Plugin policy is enabled
And the XPath-Injection-Plugin policy is enabled
And the External-redirect policy is enabled
And the CRLF-injection policy is enabled
When the scanner is run
And false positives described in: tables/false_positives.table are removed
Then no High risk vulnerabilities should be present
And no Medium risk vulnerabilities should be present