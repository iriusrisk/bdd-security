package net.continuumsecurity;

import difflib.DiffUtils;
import difflib.Patch;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	static Logger log = Logger.getLogger(Utils.class);

	public static String extractSessionIDName(String target) {
		if (Config.getSessionIDs().size() == 0) {
			log.warn("Attempting to extract session ID from string, but no session IDs defined in the configuration.");
		}
		for (String sessId : Config.getSessionIDs()) {
			Pattern p = Pattern.compile(".*"+sessId+".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = p.matcher(target);
			log.trace("Search for sessionID: "+sessId+" in string: "+target);
			if (m.matches()) {
				log.trace("\t Found.");
				return sessId;
			}
		}
		log.trace("\t Not found.");
		return null;
	}

    public static String stripTags(String html) {
        return html.replaceAll("<.*?>","");
    }

    public static int getDiffScore(String one, String two) {
        List<String> first = Arrays.asList(one.split("[\\n\\ ]+"));
        List<String> second = Arrays.asList(two.split("[\\n\\ ]+"));

        Patch p = DiffUtils.diff(first,second);
        return p.getDeltas().size();
    }
}
