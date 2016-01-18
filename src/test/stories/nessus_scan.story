Automated Nessus scanning

Meta: @story nessus_scan @skip

Narrative:
In order to identify security vulnerabilities on the hosts
As a system owner
I want to scan the hosts for known security vulnerabilities
					 
Scenario: The host systems should not expose known security vulnerabilities

Given a nessus API client that accepts all hostnames in SSL certificates
And a nessus version 6 server at https://localhost:8834
And the scanning policy named bdd-policy
And the target host names from: auto-generated/hosts.table
When the scanner is run with scan name bddscan
And the list of issues is stored
And the nessus false positives listed in: tables/nessus.false_positives.table are removed
Then no severity: 2 or higher issues should be present

