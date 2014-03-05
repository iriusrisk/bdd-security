Automated host scanning

Narrative:
In order to reduce the risk of vulnerabilities on the hosts
As an operations engineer responsible for the configuration of the host systems
I want to scan the hosts for known security vulnerabilities
					 
Scenario: The host systems should not expose known security vulnerabilities 
Given a nessus server at http://localhost:8834
And the nessus username continuum and the password continuum
And the scanning policy named test
And the target hosts
|hostname|
|baseUrl|
When the scanner is run with scan name bdd-security
And the list of issues is stored
And the following false positives are removed
|pluginID		|hostname		|
Then no medium or higher risk issues should be present
