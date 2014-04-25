
Scenario: Navigate and spider the application
Meta: @pre navigate

Given a new scanning session
And the page flow described in the method: navigate is run through the proxy
And the following URL regular expressions are excluded from the spider:
|regex|
|.*logout.*|
And the spider is configured for a maximum depth of 10
And the spider is configured for 10 concurrent threads
And the following URLs are spidered:
|url|
|baseUrl|
And the spider status reaches 100% complete

