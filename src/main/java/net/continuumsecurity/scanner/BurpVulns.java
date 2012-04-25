package net.continuumsecurity.scanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BurpVulns {
	private final static Map<String, String> map = new HashMap<String, String>();
	private static boolean loaded = false;
	private static Properties props = new Properties();
	public static final String BURP_VULNS = "burp.vulns.properties";

	private static void load() {
		if (!loaded) {
			try {
				FileInputStream fis = new FileInputStream(BURP_VULNS);
				props.load(fis);
			} catch (IOException e) {

				e.printStackTrace();
			}
			loaded = true;
		}
	}

	public static List<String> getAllVulnIds() {
		load();
		List<String> res = new ArrayList<String>();
		for (Object obj : props.keySet()) {
			res.add((String)obj);
		}
		return res;
	}
	
	

}
