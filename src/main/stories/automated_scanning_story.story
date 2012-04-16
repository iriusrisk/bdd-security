Meta:
@story Automated Scanning

Scenario: Passively scan for vulnerabilities while the application is navigated
Meta:
@id scan_passive

Given a passive scanning policy
When the application is navigated
And the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for SQL injection vulnerabilities
Meta:
@id scan_sql

Given an SQL injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Cross Site Scripting vulnerabilities
Meta:
@id scan_xss

Given a Cross Site Scripting scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Command Injection vulnerabilities
Meta:
@id scan_cmd

Given a command injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for LDAP Injection vulnerabilities
Meta:
@id scan_ldap

Given an LDAP injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for XML-SOAP Injection vulnerabilities
Meta:
@id scan_xml

Given an XML injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Miscellaneous header and server vulnerabilities
Meta:
@id scan_misc

Given a policy containing miscellaneous header and server checks
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

