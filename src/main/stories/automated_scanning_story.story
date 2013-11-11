
Description: The automated security scanner should not report any vulnerabilities in the application

Meta:
@story AutomatedScanning

Scenario: The application should not contain vulnerabilities found through automated scanning
Meta:
@Description These vulnerabilities typically include risks to the confidentiality of the user's session, or attacks which can be launched in shared browser environments
@id scan_passive

Given the scannable methods of the application are navigated
When the scanner is run
And false positives described in: false_positives.table are removed
Then no HIGH risk vulnerabilities should be present
And no MEDIUM risk vulnerabilities should be present
