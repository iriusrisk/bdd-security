package net.continuumsecurity.internal;

import net.continuumsecurity.scanner.SSLyzeParser;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyseParserWithErrorsTest {
    String output;
    SSLyzeParser parser;

    @Before
    public void setup() throws IOException {
        output = FileUtils.readFileToString(new File("src/test/resources/sslyze.errors"));
        parser = new SSLyzeParser(output);
    }

    @Test
    public void testListPreferredCipherSuites() {
        List<String> ciphers = parser.listPreferredCipherSuiteNamesFor("TLSV1_2");
        assertEquals("ECDHE-RSA-AES256-GCM-SHA384", ciphers.get(0));
        assertEquals(1,ciphers.size());

        ciphers = parser.listPreferredCipherSuiteNamesFor("TLSV1_1");
        assertEquals("ECDHE-RSA-AES256-SHA",ciphers.get(0));
        assertEquals(1,ciphers.size());

        assertEquals(0, parser.listPreferredCipherSuitesFor("SSLV2").size());
    }

    @Test
    public void testListAcceptedCipherSuites() {
        List<String> ciphers = parser.listAcceptedCipherSuiteNamesFor("TLSV1");
        assertEquals("ECDHE-RSA-AES256-SHA", ciphers.get(0));
        assertEquals(4,ciphers.size());

        ciphers = parser.listAcceptedCipherSuiteNamesFor("TLSV1_1");
        assertEquals("ECDHE-RSA-AES256-SHA",ciphers.get(0));
        assertEquals(4,ciphers.size());

        assertEquals(0, parser.listAcceptedCipherSuitesFor("SSLV2").size());
    }

    @Test
    public void testListAllSupportedProtocols() {
        List<String> suites = parser.listAllSupportedProtocols();
        assertEquals(3, suites.size());
        assertThat("TLSV1_2", isIn(suites));
        assertThat("TLSV1_1", isIn(suites));
        assertThat("TLSV1", isIn(suites));
        assertFalse(suites.contains("SSLV3"));
    }

    @Test
    public void testFindSmallestAcceptedKeySize() {
        assertEquals(128, parser.findSmallestAcceptedKeySize());
    }

    @Test
    public void testDoesAnyLineMatch() {
        assertThat(parser.doesAnyLineMatch(".*Client-initiated Renegotiations:\\s+OK\\s+-\\s+Rejected.*"),equalTo(true));
    }
}
