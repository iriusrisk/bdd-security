package net.continuumsecurity.web;

import net.continuumsecurity.Utils;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void test() {
		String burpDetail = "The URL in the request appears to contain a session token within the query string:&lt;ul&gt;&lt;li&gt;http://www.ispatula.com&lt;wbr&gt;:8081/ispatula/shop&lt;wbr&gt;/Search.do;jsessionid&lt;wbr&gt;=3FF20C6F0B7AC9D6512&lt;wbr&gt;1135052DD774B?query=hello&lt;wbr&gt;&amp;amp;=&lt;/li&gt;&lt;/ul&gt;";
		assert "jsessionid".equalsIgnoreCase(Utils.extractSessionIDName(burpDetail));
		
		burpDetail = "blahalaahahaa";
		assert Utils.extractSessionIDName(burpDetail) == null;
		
	}

    @Test
    public void testDiffScore() {
        assertEquals(1,Utils.getDiffScore("one,two three","one,two four"));
        assertEquals(2,Utils.getDiffScore("3 2 1","1 2 3"));
        assertEquals(1,Utils.getDiffScore("4 3 2 1","3 2 1"));
    }

}
