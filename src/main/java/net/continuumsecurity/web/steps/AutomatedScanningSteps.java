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
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.Utils;
import net.continuumsecurity.proxy.ScanningProxy;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.drivers.ProxyFactory;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
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
    List<Alert> alerts;

    public AutomatedScanningSteps() {
        app = Config.createApp();
        app.enableHttpLoggingClient();
        scanner = ProxyFactory.getScanningProxy();
    }


    @Given("a fresh scanner with all policies disabled")
    public void createNewScanSession() {
        scanner.clear();
        scanner.disableAllScanners();
    }

    @Given("the scannable methods of the application are navigated through the proxy")
    public void navigateApp() throws Exception {
        // Navigate through the app and record the traffic through the
        // scanner
        for (Method method : app.getScannableMethods()) {
            app.enableHttpLoggingClient();
            log.debug("Navigating method: " + method.getName());
            app.getClass().getMethod(method.getName()).invoke(app);
        }
    }

    @Given("the passive scanner is enabled")
    public void enablePassiveScanner() {
        scanner.setEnablePassiveScan(true);
    }


    @Given("the $policyName policy is enabled")
    public void enablePolicy(@Named("policyName") String policyName) {
        String ids = null;
        switch (policyName.toLowerCase()) {
            case "cross-site-scripting":
                ids = "40012,40014,40016,40017";  break;
            case "sql-injection":
                ids = "40018";   break;
            case "mysql-sql-injection":
                ids = "40019";  break;
            case "hypersonic-sql-injection":
                ids = "40020";  break;
            case "oracle-sql-injection":
                ids = "40021"; break;
            case "postgresql-sql-injection":
                ids = "40022"; break;
            case "path-traversal":
                ids = "6"; break;
            case "remote-file-inclusion":
                ids = "7"; break;
            case "source-code-disclosure":
                ids = "40"; break;
            case "url-redirector-abuse":
                ids = "20010"; break;
            case "server-side-include":
                ids = "40009"; break;
            case "ldap-injection":
                ids = "40015"; break;
            case "server-side-code-injection":
                ids = "90019";  break;
            case "remote-os-command-injection":
                ids = "90020"; break;
            case "xpath-injection":
                ids = "90021"; break;
            case "external-redirect":
                ids = "30000"; break;
            case "crlf-injection":
                ids = "40003"; break;
        }
        if (ids == null) throw new UnexpectedContentException("No matching policy found for: "+policyName);
        scanner.setEnableScanners(ids, true);
    }


    @When("the scanner is run")
    public void runScanner() throws Exception {
        scanner.scan(Config.getBaseUrl());
        int complete = 0;
        while (complete < 100) {
            complete = scanner.getPercentComplete();
            log.debug("Scan is " + complete + "% complete.");
            Thread.sleep(5000);
        }
    }

    @When("false positives described in: $falsePositives are removed")
    public void removeFalsePositives(ExamplesTable falsePositives) {
        alerts = scanner.getAlerts();
        List<Alert> clean = new ArrayList<Alert>();

        for (Alert alert : alerts) {
            boolean falsePositive = false;
            for (FalsePositive falsep : Utils.getFalsePositivesFromTable(falsePositives)) {
                if (falsep.matches(alert.getUrl(), alert.getParam(), Integer.toString(alert.getCweId()))) {
                    falsePositive = true;
                }
            }
            if (!falsePositive) clean.add(alert);
        }
        alerts = clean;
    }

    @Then("no $riskRating or higher risk vulnerabilities should be present")
    public void checkVulnerabilities(@Named("riskRating") String risk) {
        assertThat("No methods found annotated with @SecurityScan.  Nothing scanned.", app.getScannableMethods().size(), greaterThan(0));
        List<Alert> filteredAlerts = null;
        Alert.Risk riskLevel = Alert.Risk.High;

        if ("HIGH".equalsIgnoreCase(risk)) {
            riskLevel = Alert.Risk.High;
        } else if ("MEDIUM".equalsIgnoreCase(risk)) {
            riskLevel = Alert.Risk.Medium;
        } else if ("LOW".equalsIgnoreCase(risk)) {
            riskLevel = Alert.Risk.Low;
        }
        filteredAlerts = getAllAlertsByRiskRating(alerts, riskLevel);
        String details = getAlertDetails(filteredAlerts);

        assertThat(filteredAlerts.size() + " " + risk + " vulnerabilities found.\nDetails:\n" + details, filteredAlerts.size(),
                equalTo(0));
    }

    private List<Alert> getAllAlertsByRiskRating(List<Alert> alerts, Alert.Risk rating) {
        List<Alert> results = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getRisk().ordinal() >= rating.ordinal() ) results.add(alert);
        }
        return results;
    }

    private String getAlertDetails(List<Alert> alerts) {
        String detail = "";
        if (alerts.size() != 0) {
            for (Alert alert : alerts) {
                detail = detail + alert.getAlert()+"\n"
                        + "URL: "+alert.getUrl() + "\n"
                        + "Parameter: "+alert.getParam() + "\n"
                        + "CWE: " + alert.getCweId() + "\n";
            }
        }
        return detail;
    }
}
