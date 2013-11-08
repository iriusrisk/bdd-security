
Description: The automated security scanner should not report any vulnerabilities in the application

Meta:
@story Automated Scanning

Scenario: The application should not contain vulnerabilities found through automated scanning
Meta:
@Description These vulnerabilities typically include risks to the confidentiality of the user's session, or attacks which can be launched in shared browser environments
@id scan_passive

Given the scannable methods of the application are navigated
And the scanner is run
Then no vulnerabilities should be present

