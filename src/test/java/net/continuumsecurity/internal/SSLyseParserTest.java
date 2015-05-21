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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyseParserTest {
    String output;
    SSLyzeParser parser;

    @Before
    public void setup() throws IOException {
        output = FileUtils.readFileToString(new File("src/test/resources/sslyze.example"));
        parser = new SSLyzeParser(output);
    }

    @Test
    public void testListPreferredCipherSuites() {
        List<String> ciphers = parser.listPreferredCipherSuiteNamesFor("TLSV1");
        assertEquals("ECDHE-RSA-RC4-SHA", ciphers.get(0));
        assertEquals(1,ciphers.size());

        ciphers = parser.listPreferredCipherSuiteNamesFor("TLSV1_1");
        assertEquals("ECDHE-RSA-AES128-SHA",ciphers.get(0));
        assertEquals(1,ciphers.size());

        assertEquals(0, parser.listPreferredCipherSuitesFor("SSLV2").size());
    }

    @Test
    public void testListAcceptedCipherSuites() {
        List<String> ciphers = parser.listAcceptedCipherSuiteNamesFor("TLSV1");
        assertEquals("AES128-SHA", ciphers.get(0));
        assertEquals(9,ciphers.size());

        ciphers = parser.listAcceptedCipherSuiteNamesFor("TLSV1_1");
        assertEquals("AES128-SHA",ciphers.get(0));
        assertEquals(9,ciphers.size());

        assertEquals(0, parser.listAcceptedCipherSuitesFor("SSLV2").size());
    }

    @Test
    public void testListAllSupportedProtocols() {
        List<String> suites = parser.listAllSupportedProtocols();
        assertEquals(3, suites.size());
        assertEquals("TLSV1_2", suites.get(0));
        assertFalse(suites.contains("SSLV2"));
        assertFalse(suites.contains("SSLV3"));
    }

    @Test
    public void testFindSmallestAcceptedKeySize() {
        assertEquals(112, parser.findSmallestAcceptedKeySize());
    }

    @Test
    public void testDoesAnyLineMatch() {
        assertThat(parser.doesAnyLineMatch(".*Client-initiated Renegotiations:\\s+OK\\s+-\\s+Rejected.*"),equalTo(true));
    }
}
