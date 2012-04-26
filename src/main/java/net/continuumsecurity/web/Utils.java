package net.continuumsecurity.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Utils {
	static Logger log = Logger.getLogger(Utils.class);

	public static String extractSessionID(String target) {	
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
}
