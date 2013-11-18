
Description: The automated security scanner should not report any vulnerabilities in the application

Meta:
@story AutomatedScanning

Scenario: The application should not contain vulnerabilities found through automated scanning
Meta:
@id scan_active

Given the scannable methods of the application are navigated through the proxy
When the scanner is run
And false positives described in: false_positives.table are removed
Then no HIGH or MEDIUM risk vulnerabilities should be present
