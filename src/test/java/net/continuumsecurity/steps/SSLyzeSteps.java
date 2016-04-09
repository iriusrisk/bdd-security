package net.continuumsecurity.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.continuumsecurity.Config;
import net.continuumsecurity.ProcessExecutor;
import net.continuumsecurity.scanner.SSLyzeParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyzeSteps {
    ProcessExecutor sslTester;
    SSLyzeParser parser;
    final static String OUTFILENAME = "sslyze.output";

    private ProcessExecutor createSSLyzeProcess(String target, int port) throws MalformedURLException {
        List<String> cmds = new ArrayList<>();
        cmds.addAll(Arrays.asList(Config.getInstance().getSSLyze().split("\\s+")));
        if (port > -1) {
            target = target + ":" + port;
        }
        cmds.add(target);
        return new ProcessExecutor(cmds);
    }

    @Given("^the SSLyze command is run against the host (.*) on port (\\d+)$")
    public void runSSLTestsOnSecureBaseUrl(String host, int port) throws IOException {
        sslTester = createSSLyzeProcess(host, port);
        sslTester.setFilename(OUTFILENAME);
        if (!World.getInstance().isSslRunCompleted()) {
            sslTester.start();
            World.getInstance().setSslRunCompleted(true);
        }
        parser = new SSLyzeParser(sslTester.getOutput());
    }

    @Then("the output must contain the text (.*)")
    public void verifyThatOutputContainsText(String text) throws IOException {
        if (text.startsWith("\"") || text.startsWith("'")) text = text.substring(1, text.length() - 1);
        assertThat(sslTester.getOutput(), containsString(text));
    }

    @Then("^the output must contain a line that matches (.*)")
    public void verifyThatOutputMatchesRegex(String regex) throws IOException {
        if (regex.startsWith("\"") || regex.startsWith("'")) regex = regex.substring(1, regex.length() - 1);
        assertThat(parser.doesAnyLineMatch(regex), equalTo(true));
    }

    @Then("the minimum key size must be (\\d+) bits")
    public void verifyMinimumKeySize(int size) {
        assertThat(parser.findSmallestAcceptedKeySize(), greaterThanOrEqualTo(size));
    }

    @Then("the following protocols must not be supported")
    public void verifyDisabledProcotols(List<String> forbiddenProtocols) {
        List<String> supported = parser.listAllSupportedProtocols();
        for (String forbidden : forbiddenProtocols) {
            assertThat(supported, not(hasItem(forbidden)));
        }
    }

    @Then("the following protocols must be supported")
    public void verifySupportedProcotols(List<String> mandatoryProtocols) {
        List<String> supported = parser.listAllSupportedProtocols();
        for (String mandatory : mandatoryProtocols) {
            assertThat(supported, hasItem(mandatory));
        }

    }

    @Then("any of the following ciphers must be supported")
    public void verifyAnyCipherSupported(List<String> ciphers) {
        boolean foundCipher = false;
        for (String cipher : ciphers) {
            if (parser.supportsCipher(cipher))
                foundCipher = true;
        }
        assertThat(foundCipher, equalTo(true));
    }


}
