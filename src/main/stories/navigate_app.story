
Scenario: Navigate and spider the application
Meta: @pre navigate

Given a fresh scanner with all policies disabled
And the page flow described in the method: navigate is performed through the proxy
And the spider is configured for a maximum depth of 10
And the following URL regular expressions are excluded from the spider:
|regex|
|nothing|
And the spider is configured for 10 concurrent threads
When the following URLs are spidered:
|url|
|baseUrl|
|baseSecureUrl|