/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2012 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.web.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.proxy.ScanningProxy;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.drivers.ProxyFactory;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.zaproxy.clientapi.core.Alert;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class AutomatedScanningSteps {
    Logger log = Logger.getLogger(AutomatedScanningSteps.class);
    ScanningProxy scanner;
    Application app;
    String vulnName;
    List<Alert> alerts;
    private boolean navigated = false;

    public AutomatedScanningSteps() {

    }

    @BeforeStory
    public void createScanner() {
        app = Config.createApp();
        app.enableHttpLoggingClient();
        log.debug("Resetting scanner state");
        scanner = ProxyFactory.getScanningProxy();
        scanner.clear();
    }

    @BeforeScenario
    public void resetScanner() {
        scanner.clear();
    }

    @When("the scannable methods of the application are navigated")
    public void navigateApp() throws Exception {
        // Navigate through the app and record the traffic through the
        // scanner
        for (Method method : app.getScannableMethods()) {
            app.enableHttpLoggingClient();
            log.debug("Navigating method: "+method.getName());
            app.getClass().getMethod(method.getName(), null).invoke(app, null);
        }
        navigated = true;
    }

    @When("the scanner is run")
    public void runScanner() throws Exception {
        if (!navigated)
            navigateApp();
        scanner.scan(Config.getBaseUrl());
        int complete = 0;
        while (complete < 100) {
            complete = scanner.getPercentComplete();
            log.debug("Scan is " + complete + "% complete.");
            Thread.sleep(3000);
        }
        alerts = scanner.getAlerts();
        log.debug(alerts.size() + " security issues were found.");
    }

    @Then("no vulnerabilities should be present")
    public void checkVulnerabilities() {
        assertThat("No methods found annotated with @SecurityScan.  Nothing scanned.",app.getScannableMethods().size(),greaterThan(0));
        log.debug("checking for vulnerabilities");
        String detail = "";
        if (alerts.size() != 0) {
            for (Alert alert : alerts) {
                detail = detail + alert.getUrl() + "\n"
                        + alert.getParam() + "\n"
                        + alert.getAlert() + "\n\n";
            }
        }
        assertThat(alerts.size() + " " + vulnName
                + " vulnerabilities found.\n" + detail, alerts.size(),
                equalTo(0));
    }
}
