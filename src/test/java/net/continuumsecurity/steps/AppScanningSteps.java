/*******************************************************************************
 * BDD-Security, application security testing framework
 * <p/>
 * Copyright (C) `2014 Stephen de Vries`
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.continuumsecurity.Config;
import net.continuumsecurity.UnexpectedContentException;
import net.continuumsecurity.ZAPFalsePositive;
import net.continuumsecurity.behaviour.INavigable;
import net.continuumsecurity.proxy.ContextModifier;
import net.continuumsecurity.proxy.Spider;
import net.continuumsecurity.proxy.ZAProxyScanner;
import net.continuumsecurity.web.Application;
import org.apache.log4j.Logger;
import org.zaproxy.clientapi.core.Alert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AppScanningSteps {
    Logger log = Logger.getLogger(AppScanningSteps.class);
    private ZAProxyScanner scanner;
    Application app;
    List<Alert> alerts = new ArrayList<Alert>();
    String scannerIds = null;
    private final static String ZAP_CONTEXT_NAME= "Default Context";

    public AppScanningSteps() {
        app = Config.getInstance().createApp();
    }

    @Given("the passive scanner has already run during the app navigation")
    public void runPassiveScanner() {
        //Do nothing, it has already run during navigation
    }

    @Given("a new scanning session")
    public void createNewScanSession() {
        app.enableHttpLoggingClient();
    }

    @Given("all existing alerts are deleted")
    public void deleteAlerts() {
        getScanner().deleteAlerts();
        alerts.clear();
    }

    @Given("a scanner with all policies disabled")
    public void disableAllScanners() {
        getScanner().disableAllScanners();
    }

    public ZAProxyScanner getScanner() {
        if (scanner == null) {
            scanner = new ZAProxyScanner(Config.getInstance().getProxyHost(), Config.getInstance().getProxyPort(), Config.getInstance().getProxyApi());
            scanner.setAttackMode();
        }
        return scanner;
    }

    public Spider getSpider() {
        return (Spider) getScanner();
    }

    public ContextModifier getContext() {
        return (ContextModifier) getScanner();
    }

    @When("the XML report is written to the file (.*)")
    public void writeXmlReport(String path) throws IOException {
        byte[] xmlReport = scanner.getXmlReport();
        Path pathToFile = Paths.get(path);
        Files.createDirectories(pathToFile.getParent());
        Files.write(pathToFile, xmlReport);
    }

    @Then("the HTML report is written to the file (.*)")
    public void writeHtmlReport(String path) throws IOException {
        byte[] htmlReport = scanner.getHtmlReport();
        Path pathToFile = Paths.get(path);
        Files.createDirectories(pathToFile.getParent());
        Files.write(pathToFile, htmlReport);
    }

    @Given("a scanner with all policies enabled")
    public void enableAllScanners() {
        getScanner().enableAllScanners();
    }


    private void spider(String url) throws InterruptedException {
        getSpider().spider(url, true, ZAP_CONTEXT_NAME);
        int scanId = getSpider().getLastSpiderScanId();
        int complete = getSpider().getSpiderProgress(scanId);
        while (complete < 100) {
            complete = getSpider().getSpiderProgress(scanId);
            log.debug("Spidering of: " + url + " is " + complete + "% complete.");
            Thread.sleep(2000);
        }
        for (String result : getSpider().getSpiderResults(scanId)) {
            log.debug("Found Url: " + result);
        }
    }

    @Given("the passive scanner is enabled")
    public void enablePassiveScanner() {
        getScanner().setEnablePassiveScan(true);
    }


    @Given("the (\\S+) policy is enabled")
    public void enablePolicy(String policyName) {
        switch (policyName.toLowerCase()) {
            case "directory-browsing":
                scannerIds = "0";
                break;
            case "cross-site-scripting":
                scannerIds = "40012,40014,40016,40017";
                break;
            case "sql-injection":
                scannerIds = "40018";
                break;
            case "path-traversal":
                scannerIds = "6";
                break;
            case "remote-file-inclusion":
                scannerIds = "7";
                break;
            case "server-side-include":
                scannerIds = "40009";
                break;
            case "script-active-scan-rules":
                scannerIds = "50000";
                break;
            case "server-side-code-injection":
                scannerIds = "90019";
                break;
            case "remote-os-command-injection":
                scannerIds = "90020";
                break;
            case "external-redirect":
                scannerIds = "20019";
                break;
            case "crlf-injection":
                scannerIds = "40003";
                break;
            case "source-code-disclosure":
                scannerIds = "42,10045,20017";
                break;
            case "shell-shock":
                scannerIds = "10048";
                break;
            case "remote-code-execution":
                scannerIds = "20018";
                break;
            case "ldap-injection":
                scannerIds = "40015";
                break;
            case "xpath-injection":
                scannerIds = "90021";
                break;
            case "xml-external-entity":
                scannerIds = "90023";
                break;
            case "padding-oracle":
                scannerIds = "90024";
                break;
            case "el-injection":
                scannerIds = "90025";
                break;
            case "insecure-http-methods":
                scannerIds = "90028";
                break;
            case "parameter-pollution":
                scannerIds = "20014";
                break;
            default:
                throw new RuntimeException("No policy found for: " + policyName);

        }
        if (scannerIds == null) throw new UnexpectedContentException("No matching policy found for: " + policyName);
        getScanner().setEnableScanners(scannerIds, true);
    }

    @Given("the attack strength is set to (\\S+)")
    public void setAttackStrength(String strength) {
        if (scannerIds == null)
            throw new RuntimeException("First set the scanning policy before setting attack strength or alert threshold");
        for (String id : scannerIds.split(",")) {
            getScanner().setScannerAttackStrength(id, strength.toUpperCase());

        }
    }

    @Given("the alert threshold is set to (\\S+)")
    public void setAlertThreshold(String threshold) {
        if (scannerIds == null)
            throw new RuntimeException("First set the scanning policy before setting attack strength or alert threshold");
        for (String id : scannerIds.split(",")) {
            getScanner().setScannerAlertThreshold(id, threshold.toUpperCase());
        }
    }

    @Given("the following URL regular expressions are excluded from the scanner")
    public void excludeUrlsFromScan(List<String> excludedRegexes) {
        for (String excluded : excludedRegexes) {
            getScanner().excludeFromScanner(excluded);
        }
    }

    @When("^the scanner is run$")
    public void runScanner() throws Exception {
        log.info("Scanning: " + Config.getInstance().getBaseUrl());
        getScanner().scan(Config.getInstance().getBaseUrl());
        int complete = 0;
        int scanId = getScanner().getLastScannerScanId();
        while (complete < 100) {
            complete = getScanner().getScanProgress(scanId);
            log.debug("Scan is " + complete + "% complete.");
            Thread.sleep(1000);
        }
    }

    @When("the following false positives are removed")
    public void removeFalsePositives(List<ZAPFalsePositive> falsePositives) {
        alerts = getScanner().getAlerts();
        List<Alert> validFindings = new ArrayList<>();
        validFindings.addAll(alerts);
        for (Alert alert : alerts) {
            for (ZAPFalsePositive zapFalsePositive : falsePositives) {
                if (zapFalsePositive.matches(alert.getUrl(), alert.getParam(), alert.getCweId(), alert.getWascId())) {
                    validFindings.remove(alert);
                }
            }
        }
        alerts = validFindings;
    }

    @Then("^no (\\S+) or higher risk vulnerabilities should be present$")
    public void checkVulnerabilities(String risk) {
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

    private void waitForSpiderToComplete() {
        int status = 0;
        int counter99 = 0; //hack to detect a ZAP spider that gets stuck on 99%
        int scanId = getSpider().getLastSpiderScanId();
        while (status < 100) {
            status = getSpider().getSpiderProgress(scanId);
            if (status == 99) {
                counter99++;
            }
            if (counter99 > 10) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Alert> getAllAlertsByRiskRating(List<Alert> alerts, Alert.Risk rating) {
        List<Alert> results = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getRisk().ordinal() >= rating.ordinal()) results.add(alert);
        }
        return results;
    }

    private String getAlertDetails(List<Alert> alerts) {
        String detail = "";
        if (alerts.size() != 0) {
            for (Alert alert : alerts) {
                detail = detail + alert.getName() + "\n"
                        + "URL: " + alert.getUrl() + "\n"
                        + "Parameter: " + alert.getParam() + "\n"
                        + "CWE-ID: " + alert.getCweId() + "\n"
                        + "WASC-ID: " + alert.getWascId() + "\n";
            }
        }
        return detail;
    }

    public boolean alertsMatchByValue(Alert first, Alert second) {
        //The built in Alert.matches(Alert) method includes risk, reliability and alert, but not cweid.
        if (first.getCweId() != second.getCweId()) return false;
        if (!first.getParam().equals(second.getParam())) return false;
        if (!first.getUrl().equals(second.getUrl())) return false;
        if (!first.matches(second)) return false;
        return true;
    }


    public boolean containsAlertByValue(List<Alert> alerts, Alert alert) {
        boolean found = false;
        for (Alert existing : alerts) {
            if (alertsMatchByValue(alert, existing)) {
                found = true;
                break;
            }
        }
        return found;
    }

    @And("^the navigation and spider status is reset$")
    public void setAppNotNavigatedNorSpidered() {
        World.getInstance().setNavigated(false);
        World.getInstance().setSpidered(false);
    }

    @And("^the application is navigated$")
    public void navigateAppIfNotAlreadyNavigated() {
        if (!World.getInstance().isNavigated()) {
            if (!(app instanceof INavigable))
                throw new RuntimeException("The application must implement the 'INavigable' interface to be navigable");
            app.enableHttpLoggingClient();
            log.debug("Navigating");
            ((INavigable) app).navigate();
            World.getInstance().setNavigated(true);
        }
    }

    @And("^the application is spidered$")
    public void theApplicationIsSpidered() {
        if (!World.getInstance().isSpidered()) {
            for (String regex : Config.getInstance().getIgnoreUrls()) {
                getSpider().excludeFromSpider(regex);
            }
            try {
                getContext().setIncludeInContext(ZAP_CONTEXT_NAME, ".*"); //if URLs are not in context then they won't be spidered
            } catch (Exception e) {
                e.printStackTrace();
            }
	    int maxDepth = Config.getInstance().getMaxDepth();
            getSpider().setMaxDepth(maxDepth);
            getSpider().setThreadCount(10);
            for (String url : Config.getInstance().getSpiderUrls()) {
                if (url.equalsIgnoreCase("baseurl")) url = Config.getInstance().getBaseUrl();
                try {
                    spider(url);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waitForSpiderToComplete();
            World.getInstance().setSpidered(true);
        }
    }
}
