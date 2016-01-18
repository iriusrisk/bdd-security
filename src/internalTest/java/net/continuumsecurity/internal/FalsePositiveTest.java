package net.continuumsecurity.internal;

import net.continuumsecurity.FalsePositive;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by stephen on 27/10/2015.
 */
public class FalsePositiveTest {

    @Test
    public void testMatchesAnyUrl() {
        FalsePositive falsePositive = new FalsePositive(".*","param",0,0);
        assertTrue(falsePositive.matches("http://anyurl.com","param",0,0));
    }

    @Test
    public void testMatchesAnyUrlAndAnyParam() {
        FalsePositive falsePositive = new FalsePositive(".*",".*","0","0");
        assertTrue(falsePositive.matches("http://anyurl.com","param",0,0));

        falsePositive = new FalsePositive(".*",".*","0","");
        assertTrue(falsePositive.matches("http://anyurl.com","param",0,0));
    }

    @Test
    public void testMatchesAnyUrlAndAnyParamButNotCWEID() {
        FalsePositive falsePositive = new FalsePositive(".*",".*","0",null);
        assertFalse(falsePositive.matches("http://anyurl.com", "param", 34,0));
    }
}
