
Description: The automated security scanner should not report any vulnerabilities in the application

Meta:
@story Automated Scanning

Scenario: Passively scan for vulnerabilities while the application is navigated
Meta:
@Description These vulnerabilities typically include risks to the confidentiality of the user's session, or attacks which can be launched in shared browser environments
@id scan_passive

Given a passive scanning policy
When the scannable methods of the application are navigated
And the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for SQL injection vulnerabilities
Meta:
@Description SQL injection issues pose serious risks to the confidentiality and integrity of the database since they could be used to read arbitrary data and edit and delete data
@id scan_sql

Given an SQL injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Cross Site Scripting vulnerabilities
Meta:
@Description Cross Site Scripting vulnerabilities could allow attackers to steal a users session and then impersonate them or trick users into disclosing sensitive data
@id scan_xss

Given a Cross Site Scripting scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Command Injection vulnerabilities
Meta:
@Description These types of vulnerabilities could allow attackers to execute arbitrary commands on the vulnerable server
@id scan_cmd

Given a command injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for LDAP Injection vulnerabilities
Meta:
@Description LDAP injection vulnerabilities could allow attackers to manipulate the logic of LDAP queries and potentially gain unauthorised access to data 
@id scan_ldap

Given an LDAP injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for XML-SOAP Injection vulnerabilities
Meta:
@Description XML injection could allow attackers to change the semantics of XML queries and potentially gain access to unauthorised data, or cause denial of service conditions in the XML parsers
@id scan_xml

Given an XML injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: Scan for Miscellaneous header and server vulnerabilities
Meta:
@Description These issues include vulnerabilities that would allow attackers to inject HTTP headers potentially allowing for session hijack as well as other miscellaneous vulnerabilities on the server 
@id scan_misc

Given a policy containing miscellaneous header and server checks
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

