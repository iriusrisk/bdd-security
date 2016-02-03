package net.continuumsecurity.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.continuumsecurity.Config;
import net.continuumsecurity.ProcessExecutor;
import net.continuumsecurity.Utils;
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
    final static String OUTFILENAME="sslyze.output";

    private ProcessExecutor createSSLyzeProcess() throws MalformedURLException {
        List<String> cmds = new ArrayList<>();
        cmds.addAll(Arrays.asList(Config.getInstance().getSSLyze().split("\\s+")));
        int port = Utils.getPortFromUrl(Config.getInstance().getBaseSecureUrl());

        String target = Utils.getHostFromUrl(Config.getInstance().getBaseSecureUrl());
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

    @Then("the following protocols must not be supported: (.*)")
    public void verifyDisabledProcotols(String prot) {
        List<String> supported = parser.listAllSupportedProtocols();
        assertThat(supported, not(hasItem(prot)));
    }

    @Then("the following protocols must be supported (.*)")
    public void verifySupportedProcotols(String proto) {
        List<String> supported = parser.listAllSupportedProtocols();
        assertThat(supported, hasItem(proto));

    }

    @Then("any of the following ciphers must be supported (.*)")
    public void verifyAnyCipherSupported(String cipher) {
        boolean foundCipher = false;

        if (parser.supportsCipher(cipher))
                foundCipher = true;

        assertThat(foundCipher, equalTo(true));
    }


}
