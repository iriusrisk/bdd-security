package net.continuumsecurity.steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.continuumsecurity.Config;
import net.continuumsecurity.jsslyze.JSSLyze;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyzeSteps {
    final static String OUTFILENAME = "sslyze.output";
    static String host=null;
    static int port=443;

    @When("the SSLyze command is run against the application")
    public void runSSLTestsOnSecureBaseUrl() throws IOException {
        if (!World.getInstance().isSslRunCompleted()) {
            port = Config.getInstance().getSslPort();
            host= Config.getInstance().getSslHost();
            JSSLyze jSSLLyze = new JSSLyze(Config.getInstance().getSSLyzePath(), OUTFILENAME);
            jSSLLyze.execute(Config.getInstance().getSSLyzeOption(),host,port);
            World.getInstance().setjSSLyze(jSSLLyze);
            World.getInstance().setSslRunCompleted(true);
        }
    }

    @Then("the output must contain the text (.*)")
    public void verifyThatOutputContainsText(String text) throws IOException {
        if (text.startsWith("\"") || text.startsWith("'")) text = text.substring(1, text.length() - 1);
        assertThat(World.getInstance().getjSSLyze().getOutput(), containsString(text));
    }

    @Then("^the output must contain a line that matches (.*)")
    public void verifyThatOutputMatchesRegex(String regex) throws IOException {
        if (regex.startsWith("\"") || regex.startsWith("'")) regex = regex.substring(1, regex.length() - 1);
        assertThat(World.getInstance().getjSSLyze().getParser().doesAnyLineMatch(regex), equalTo(true));
    }

    @Then("the minimum key size must be (\\d+) bits")
    public void verifyMinimumKeySize(int size) {
        assertThat(World.getInstance().getjSSLyze().getParser().findSmallestAcceptedKeySize(), greaterThanOrEqualTo(size));
    }

    @Then("the following protocols must not be supported")
    public void verifyDisabledProcotols(List<String> forbiddenProtocols) {
        List<String> supported = World.getInstance().getjSSLyze().getParser().listAllSupportedProtocols();
        for (String forbidden : forbiddenProtocols) {
            assertThat(supported, not(hasItem(forbidden)));
        }
    }

    @Then("the following protocols must be supported")
    public void verifySupportedProcotols(List<String> mandatoryProtocols) {
        List<String> supported = World.getInstance().getjSSLyze().getParser().listAllSupportedProtocols();
        for (String mandatory : mandatoryProtocols) {
            assertThat(supported, hasItem(mandatory));
        }

    }

    @Then("any of the following ciphers must be supported")
    public void verifyAnyCipherSupported(List<String> ciphers) {
        boolean foundCipher = false;
        for (String cipher : ciphers) {
            if (World.getInstance().getjSSLyze().getParser().acceptsCipherWithPartialName(cipher))
                foundCipher = true;
        }
        assertThat(foundCipher, equalTo(true));
    }


}
