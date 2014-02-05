Description: The host should be securely deployed and only expose the minimum services and ports required
Meta: @story Host

Scenario: Only the required ports should be open
Meta: @id open_ports

Given the target host from the base URL
When TCP ports from 1 to 65535 are scanned using 100 threads and a timeout of 300 milliseconds
And the open ports are selected
Then only the following ports should be open:
|port|
|80|
|443|
