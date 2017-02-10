
![](https://www.continuumsecurity.net/wp-content/uploads/2016/10/bdd-security160.png) 

Powered by <img src="https://cucumber.io/images/cucumber-logo.svg" width="200"/>

BDD-Security is a security testing framework that uses Behaviour Driven Development concepts to create self-verifying security specifications.

The framework is essentially a set of Cucumber-JVM features that are pre-wired with Selenium/WebDriver, [OWASP ZAP](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project), [SSLyze](https://github.com/nabla-c0d3/sslyze) and [Tennable's Nessus scanner](http://www.tenable.com/products/nessus-vulnerability-scanner).

It tests Web Applications and API's from an external point of view and does not require access to the target source code.

[Documentation on the Wiki](https://github.com/continuumsecurity/bdd-security/wiki)

## Version 2.1 Changelog
- Upgraded to OWASP ZAP 2.5.0
- Upgraded ZAP Client API to 1.0.0 from maven central

## Version 2.0 Changelog
- Cucumber-JVM replaced JBehave
- Gradle replaced Ant
- Rearranged files to fit Gradle/Maven conventions
- Removed command line runners. Tests run from gradle

Legacy JBehave version is available on the jbehave branch

## v0.9.2 Changelog
- Integrated with OWASP ZAP 2.4.3.
- Support setting an API KEY for ZAP

## v0.9.1 Changelog
- HtmlUnitDriver support, it is also the default driver if no other driver is specified in config.xml.  BIG speed improvements.
- Support for testing non-browser based web services and APIs.  See the [getting started guide](https://github.com/continuumsecurity/bdd-security/wiki/2-Getting-Started) for more details.
- Removed all TestNG tests.

## v0.9 Changelog
- Moved tables that are auto-generated during startup into the stories/auto-generated folder. Tables that are user editable stay in the stories/tables folder.
- Hosts and expected open ports are defined in the config.xml.  Nessus and port scanning stories now read the target data from these files
- Moved the Nessus false positives to tables/nessus.false_positives.table
- Moved the OWASP ZAP false positives to tables/zap.false_positives.table
- Fixed bug in the portscan story
- Enabled portscanning of multiple hosts
