package net.continuumsecurity.proxy;

import edu.umass.cs.benchlab.har.HarHeader;
import edu.umass.cs.benchlab.har.HarRequest;

public class HarUtils {
    public static HarRequest changeCookieValue(HarRequest request,String name,String value) {
        String patternMulti = "([; ]" + name + ")=[^;]*(.*)";
        String patternStart = "^(" + name + ")=[^;]*(.*)";

        for (HarHeader header : request.getHeaders().getHeaders()) {
            if (header.getName().equalsIgnoreCase("COOKIE")) {
                if (header.getValue() != null) {
                    String updated = header.getValue().replaceAll(patternMulti, "$1=" + value + "$2");
                    if (updated.equals(header.getValue())) {
                        updated = header.getValue().replaceAll(patternStart, "$1=" + value + "$2");
                    }
                    header.setValue(updated);
                }
            }
        }
        return request;
    }
}
