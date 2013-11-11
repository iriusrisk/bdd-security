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
import net.continuumsecurity.FalsePositive;
import net.continuumsecurity.Utils;
import net.continuumsecurity.proxy.ScanningProxy;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.drivers.ProxyFactory;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.zaproxy.clientapi.core.Alert;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    @Given("the scannable methods of the application are navigated through the proxy")
    public void navigateApp() throws Exception {
        // Navigate through the app and record the traffic through the
        // scanner
        for (Method method : app.getScannableMethods()) {
            app.enableHttpLoggingClient();
            log.debug("Navigating method: "+method.getName());
            app.getClass().getMethod(method.getName()).invoke(app);
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
    }

    @When("false positives described in: $falsePositives are removed")
    public void removeFalsePositives(ExamplesTable falsePositives) {
        alerts = scanner.getAlerts();
        List<Alert> clean = new ArrayList<Alert>();

        for (Alert alert : alerts) {
            boolean falsePositive = false;
            for (FalsePositive falsep : Utils.getFalsePositivesFromTable(falsePositives)) {
                if (falsep.matches(alert.getUrl(),alert.getParam(),Integer.toString(alert.getCweId()))) {
                    falsePositive = true;
                }
            }
            if (!falsePositive) clean.add(alert);
        }
        alerts = clean;
    }

    @Then("no HIGH risk vulnerabilities should be present")
    public void checkHighRiskVulnerabilities() {
        assertThat("No methods found annotated with @SecurityScan.  Nothing scanned.", app.getScannableMethods().size(), greaterThan(0));
        String detail = "";

        List<Alert> high = getAllAlertsByRiskRating(alerts, Alert.Risk.High);
        detail = getAlertDetails(high);

        assertThat(high.size() + " high risk vulnerabilities found.\n" + detail, high.size(),
                equalTo(0));
    }

    @Then("no MEDIUM risk vulnerabilities should be present")
    public void checkMediumRiskVulnerabilities() {
        assertThat("No methods found annotated with @SecurityScan.  Nothing scanned.", app.getScannableMethods().size(), greaterThan(0));
        String detail = "";

        List<Alert> medium = getAllAlertsByRiskRating(alerts, Alert.Risk.High);
        detail = getAlertDetails(medium);

        assertThat(medium.size() + " medium risk vulnerabilities found.\n" + detail, medium.size(),
                equalTo(0));
    }

    private List<Alert> getAllAlertsByRiskRating(List<Alert> alerts, Alert.Risk rating) {
        List<Alert> results = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getRisk().equals(rating)) results.add(alert);
        }
        return results;
    }

    private String getAlertDetails(List<Alert> alerts) {
        String detail = "";
        if (alerts.size() != 0) {
            for (Alert alert : alerts) {

                detail = detail + alert.getUrl() + "\n"
                        + alert.getParam() + "\n"
                        + alert.getAlert() + "\n\n";
            }
        }
        return detail;
    }
}
