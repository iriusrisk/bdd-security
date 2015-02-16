package net.continuumsecurity.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.ProcessExecutor;
import net.continuumsecurity.Utils;
import net.continuumsecurity.scanner.SSLyzeParser;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Predicates.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyzeSteps {
    ProcessExecutor sslTester;
    SSLyzeParser parser;
    final static String OUTFILENAME="sslyze.output";

    private ProcessExecutor createSSLyzeProcess() throws MalformedURLException {
        List<String> cmds = new ArrayList<>();
        cmds.addAll(Arrays.asList(Config.getSSLyze().split("\\s+")));
        int port = Utils.getPortFromUrl(Config.getBaseSecureUrl());

        String target = Utils.getHostFromUrl(Config.getBaseSecureUrl());
        if (port > -1) {
            target = target+":"+port;
        }
        cmds.add(target);
        return new ProcessExecutor(cmds);
    }

    @Given("the SSLyze command is run against the secure base Url")
    public void runSSLTestsOnSecureBaseUrl() throws IOException {
        if (sslTester == null) {
            sslTester = createSSLyzeProcess();
            sslTester.setOutputFile(OUTFILENAME);
            sslTester.start();
            parser = new SSLyzeParser(sslTester.getOutput());
        }
    }
    
    @Then("the output must contain the text $text")
    public void verifyThatOutputContainsText(String text) throws IOException {
        if (text.startsWith("\"") || text.startsWith("'")) text = text.substring(1,text.length()-1);
        assertThat(sslTester.getOutput(), containsString(text));
    }

    @Then("the output must contain a line that matches the regular expression $text")
    public void verifyThatOutputMatchesRegex(String regex) throws IOException {
        if (regex.startsWith("\"") || regex.startsWith("'")) regex = regex.substring(1,regex.length()-1);
        assertThat(parser.doesAnyLineMatch(regex), equalTo(true));
    }

    @Then("the minimum key size must be $size bits")
    public void verifyMinimumKeySize(int size) {
        assertThat(parser.findSmallestAcceptedKeySize(), greaterThanOrEqualTo(size));
    }

    @Then("the following protocols must not be supported $protocols")
    public void verifyDisabledProcotols(ExamplesTable table) {
        List<String> supported = parser.listAllSupportedProtocols();
        for (Parameters param : table.getRowsAsParameters()) {
            String proto = param.values().get("protocol");
            assertThat(supported, not(hasItem(proto)));
        }
    }

    @Then("the following protocols must be supported $protocols")
    public void verifySupportedProcotols(ExamplesTable table) {
        List<String> supported = parser.listAllSupportedProtocols();
        for (Parameters param : table.getRowsAsParameters()) {
            String proto = param.values().get("protocol");
            assertThat(supported, hasItem(proto));
        }
    }

    @Then("any of the following ciphers must be supported $ciphers")
    public void verifyAnyCipherSupported(ExamplesTable table) {
        boolean foundCipher = false;
        for (Parameters params : table.getRowsAsParameters()) {
            if (parser.supportsCipher(params.values().get("cipher"))) foundCipher = true;
        }
        assertThat(foundCipher, equalTo(true));
    }


}
