Automated Nessus scanning

Meta: @story nessus_scan @skip

Narrative:
In order to identify security vulnerabilities on the hosts
As a system owner
I want to scan the hosts for known security vulnerabilities
					 
Scenario: The host systems should not expose known security vulnerabilities

Given a nessus version 6 server at https://localhost:8834
And the nessus username continuum and the password continuum
And the scanning policy named test
And the target hosts
|hostname			|
|localhost			|
When the scanner is run with scan name bddscan
And the list of issues is stored
And the following false positives are removed
|PluginID		|Hostname		|Reason											        |
|43111          |127.0.0.1      |Example of how to add a false positive to this story   |
Then no severity: 2 or higher issues should be present

