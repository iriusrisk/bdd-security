# Release
Version: 0.9

## Changes
- Moved tables that are auto-generated during startup into the stories/auto-generated folder. Tables that are user editable stay in the stories/tables folder.
- Hosts and expected open ports are defined in the config.xml.  Nessus and port scanning stories now read the target data from these files
- Moved the Nessus false positives to tables/nessus.false_positives.table
- Moved the OWASP ZAP false positives to tables/zap.false_positives.table
- Fixed bug in the portscan story
- Enabled portscanning of multiple hosts


# Overview
BDD-Security is a framework written in Java and based on JBehave and Selenium 2 (WebDriver) that uses predefined security tests and an integrated security scanner to perform automated security assessments of web applications.

- [Detailed introduction](http://www.continuumsecurity.net/bdd-intro.html)
- [Getting Started](http://www.continuumsecurity.net/bdd-getstarted.html)
- [Tutorial](http://www.continuumsecurity.net/bdd-tut.html)

