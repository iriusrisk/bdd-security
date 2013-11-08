package net.continuumsecurity;

import com.rits.cloning.Cloner;
import difflib.DiffUtils;
import difflib.Patch;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarHeader;
import edu.umass.cs.benchlab.har.HarRequest;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static Logger log = Logger.getLogger(Utils.class);

    public static String extractSessionIDName(String target) {
        if (Config.getSessionIDs().size() == 0) {
            log.warn("Attempting to extract session ID from string, but no session IDs defined in the configuration.");
        }
        for (String sessId : Config.getSessionIDs()) {
            Pattern p = Pattern.compile(".*" + sessId + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
            Matcher m = p.matcher(target);
            log.trace("Search for sessionID: " + sessId + " in string: " + target);
            if (m.matches()) {
                log.trace("\t Found.");
                return sessId;
            }
        }
        log.trace("\t Not found.");
        return null;
    }

    public static String stripTags(String html) {
        return html.replaceAll("<.*?>", "");
    }

    public static int getDiffScore(String one, String two) {
        List<String> first = Arrays.asList(one.split("[\\n\\ ]+"));
        List<String> second = Arrays.asList(two.split("[\\n\\ ]+"));

        Patch p = DiffUtils.diff(first, second);
        return p.getDeltas().size();
    }

    public static HarEntry copyHarEntry(HarEntry entry) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Cloner cloner = new Cloner();
        return cloner.deepClone(entry);
    }

    public static HarRequest replaceCookies(HarRequest request, Map<String, String> cookieMap) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String name : cookieMap.keySet()) {
            System.out.println("Changing cookie name: " + name + " to Value: " + cookieMap.get(name));
            request = changeCookieValue(request, name, cookieMap.get(name));
        }
        return request;
    }

    public static HarRequest changeCookieValue(HarRequest request, String name, String value) {
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
