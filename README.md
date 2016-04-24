
BDD-Security migration to Cucumber and Gradle
=================================================
Current working branch for Cucumber migration, expect much brokeness while we complete the migration.

Configuration uses same concepts as the JBehave version: config.xml and the bespoke Java class file for recording selenium steps.
Tests should be run through the JUnit runners.

# Getting Started
## Launch the application to test
* Download the RopeyTasks vulnerable application: https://github.com/continuumsecurity/RopeyTasks
* Start the vulnerable app: java -jar ropeytasks.jar

## Run BDD-Security tests
* Checkout the BDD-Security cukesecure branch
* (The default config.xml and RopeyTasksApplication.java are already configured to test that vulnerable app)
* Run a single BDD-Security feature: ./gradlew -Dtest.single=AuthenticationTest test

