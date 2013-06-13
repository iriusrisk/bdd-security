
Description: The automated security scanner should not report any vulnerabilities in the application

Meta:
@story Automated Scanning

Scenario: The application should not contain vulnerabilities found through passive scanning
Meta:
@Description These vulnerabilities typically include risks to the confidentiality of the user's session, or attacks which can be launched in shared browser environments
@id scan_passive

Given a passive scanning policy
When the scannable methods of the application are navigated
And the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no SQL injection vulnerabilities present
Meta:
@Description SQL injection issues pose serious risks to the confidentiality and integrity of the database since they could be used to read arbitrary data and edit and delete data
@Reference WASC-19 http://projects.webappsec.org/w/page/13246963/SQL%20Injection
@id scan_sql

Given an SQL injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no Cross Site Scripting vulnerabilities present
Meta:
@Description Cross Site Scripting vulnerabilities could allow attackers to steal a users session and then impersonate them or trick users into disclosing sensitive data
@Reference WASC-8 http://projects.webappsec.org/w/page/13246920/Cross%20Site%20Scripting
@id scan_xss

Given a Cross Site Scripting scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no Command Injection vulnerabilities present
Meta:
@Description These types of vulnerabilities could allow attackers to execute arbitrary commands on the vulnerable server
@Reference WASC-31 http://projects.webappsec.org/w/page/13246950/OS%20Commanding
@id scan_cmd

Given a command injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no LDAP Injection vulnerabilities present
Meta:
@Description LDAP injection vulnerabilities could allow attackers to manipulate the logic of LDAP queries and potentially gain unauthorised access to data
@Reference WASC-29 http://projects.webappsec.org/w/page/13246947/LDAP%20Injection
@id scan_ldap

Given an LDAP injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no XML-SOAP Injection vulnerabilities present
Meta:
@Description XML injection could allow attackers to change the semantics of XML queries and potentially gain access to unauthorised data, or cause denial of service conditions in the XML parsers     ]
@Reference WASC-23 http://projects.webappsec.org/w/page/13247004/XML%20Injection
@id scan_xml

Given an XML injection scanning policy
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

Scenario: There should be no Miscellaneous header and server vulnerabilities present
Meta:
@Description These issues include vulnerabilities that would allow attackers to inject HTTP headers potentially allowing for session hijack as well as other miscellaneous vulnerabilities on the server 
@id scan_misc

Given a policy containing miscellaneous header and server checks
And scanning of all injection points is enabled
When the scanner is run
Then no vulnerabilities should be present

