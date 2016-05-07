
![](http://www.continuumsecurity.net/images/bdd-security-logo-small.png)

BDD-Security is a security testing framework that uses Behaviour Driven Development concepts to create self-verifying security specifications.

The framework is essentially a set of [Cucumber-JVM](http://cucumber.io) features that are pre-wired with Selenium/WebDriver, [OWASP ZAP](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project), [SSLyze](https://github.com/nabla-c0d3/sslyze) and [Tennable's Nessus scanner](http://www.tenable.com/products/nessus-vulnerability-scanner).

It tests Web Applications and API's from an external point of view and does not require access to the target source code.

BDD-Security uses the [Gradle](http://www.gradle.org) build system.

[Documentation on the Wiki](https://github.com/continuumsecurity/bdd-security/wiki)

## Legacy JBehave version
The old version is available under the jbehave branch and will no longer be updated