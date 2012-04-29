/*******************************************************************************
 *    BDD-Security, application security testing framework
 * 
 * Copyright (C) `2012 Stephen de Vries`
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity;

import net.continuumsecurity.web.WebApplication;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

	public static WebApplication createApp(WebDriver driver) {
		Object app = null;
		try {
			Class appClass = Class.forName(Config.getClassName());
			Constructor constructor = appClass.getConstructor(WebDriver.class);
			app = constructor.newInstance(driver);
			if (!(app instanceof WebApplication)) {
				System.err.println("FATAL error: The defined class: "
						+ Config.getClassName()
						+ " does not extend WebApplication.");
				System.exit(1);
			}
			return (WebApplication) app;
		} catch (Exception e) {
			System.err.println("FATAL error instantiating the class: "
					+ Config.getClassName());
			e.printStackTrace();
			System.exit(1);
		}
		return (WebApplication) app;
	}

	public static Config instance() {
		if (config == null) {
			config = new Config();
			config.initialiseTables();
		}
		return config;
	}

	private Config() {
		PropertyConfigurator.configure("log4j.properties");
		loadConfig("config.xml");
	}
	
	public synchronized void initialiseTables() {	
		writeTable(getStoryDir() +"users.table", usersToTable(getUsers()));
		writeTable(getStoryDir() + "tables"+File.separator+"authorised.resources.table",
				authorisedResourcesToTable(createApp(null), getUsers()));
		writeTable(getStoryDir() + "tables"+File.separator+"unauthorised.resources.table",
				unAuthorisedResourcesToTable(createApp(null), getUsers()));
	}

	protected static Config config;

	public synchronized Users getUsers() {
		Users users = new Users();

		List<HierarchicalConfiguration> usersInXml = xml
				.configurationsAt("users.user");
		for (HierarchicalConfiguration user : usersInXml) {
			User theUser = new User(new UserPassCredentials(
					user.getString("[@username]"),
					user.getString("[@password]")));
			List<String> roles = new ArrayList<String>();
			for (Object o : user.getList("role")) {
				roles.add((String) o);
			}
			theUser.setRoles(roles);
			//There's got to be a cleaner way to do this...
			List names = user.getList("recoverpassword[@name]");
			List values = user.getList("recoverpassword[@value]");
			Map<String,String> recoverMap = new HashMap<String,String>();
			for (int i=0;i < names.size();i++) {
				recoverMap.put((String)names.get(i), (String)values.get(i));
			}
			theUser.setRecoverPasswordMap(recoverMap);
			users.add(theUser);
		}
		return users;
	}

	protected XMLConfiguration xml;

	public static String getClassName() {
		return getXml().getString("class");
	}

	public static String getBaseUrl() {
		return getXml().getString("baseUrl");
	}

	public static String getSecureBaseUrl() {
		return getXml().getString("secureBaseUrl");
	}

	public static String getDefaultDriver() {
		return getXml().getString("defaultDriver");
	}

	public static String getBurpDriver() {
		return getXml().getString("burpDriver");
	}

	public static String getBurpHost() {
		return getXml().getString("burp.host");
	}
	
	public static boolean displayStackTrace() {
		return getXml().getBoolean("displayStackTrace");
	}
	
	public static String getIncorrectUsername() {
		return getXml().getString("incorrectUsername");
	}
	
	public static String getIncorrectPassword() {
		return getXml().getString("incorrectPassword");
	}

	public static int getBurpPort() {
		return getXml().getInt("burp.port");
	}

	public static String getBurpWSUrl() {
		return getXml().getString("burp.WSUrl");
	}

	public static String getLatestReportsDir() {
		return getXml().getString("latestReportsDir");
	}

	public static String getReportsDir() {
		return getXml().getString("reportsDir");
	}

	public static List<String> getSessionIDs() {
		List<String> ids = new ArrayList<String>();
		for (Object o : getXml().getList("sessionIds.name")) {
			ids.add((String) o);
		}
		return ids;
	}

	public static String getBurpWSProxyHost() {
		try {
			return getXml().getString("burp.WSProxyHost");
		} catch (java.util.NoSuchElementException nse) {
			return null;
		}
	}

	public static int getBurpWSProxyPort() {
		try {
			return getXml().getInt("burp.WSProxyPort");
		} catch (java.util.NoSuchElementException nse) {
			return 0;
		} catch (org.apache.commons.configuration.ConversionException ce) {
            return 0;
        }
	}

	public static void setXml(XMLConfiguration xml) {
		instance().xml = xml;
	}

	//Not used yet
	public static String getUpstreamProxyHost() {
		return getXml().getString("upstreamproxyHost");
	}
	
	//Not used yet
	public static int getUpstreamProxyPort() {
		return getXml().getInt("upstreamproxyPort");
	}
	
	
	public static String getStoryDir() {
		return System.getProperty("user.dir")
				+ File.separator
				+ getXml().getString("storyDir");
	}
	
	public static String getStoryUrl() {
		return "file:///" + getStoryDir();
	}

	public synchronized static XMLConfiguration getXml() {
		return instance().xml;
	}

	public void loadConfig(String file) {
		try {
			xml = new XMLConfiguration();
			xml.load(file);

		} catch (ConfigurationException cex) {
			cex.printStackTrace();
			System.exit(1);
		}
	}

	public static synchronized List<List<String>> usersToTable(Users users) {
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("username");
		row.add("password");
		row.add("roles");
		table.add(row);
		for (User user : users.getAll()) {
			row = new ArrayList<String>();
			row.add(user.getCredentials().get("username"));
			row.add(user.getCredentials().get("password"));
			row.add(user.getRolesAsCSV());
			table.add(row);
		}
		return table;
	}

	public static synchronized List<List<String>> authorisedResourcesToTable(
			WebApplication app, Users users) {
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("method");
		row.add("username");
		row.add("password");
        row.add("verifyString");
		table.add(row);
		for (Method method : app.getRestrictedMethods()) {
			for (String role : app.getAuthorisedRoles(method.getName())) {
				row = new ArrayList<String>();
				row.add(method.getName());
				row.add(users.getDefaultCredentials(role).get("username"));
				row.add(users.getDefaultCredentials(role).get("password"));
                row.add(method.getAnnotation(Restricted.class).verifyWithText());
				table.add(row);
			}
		}
		return table;
	}

	/*
	 * Create a matrix of restricted methods and the users who are not
	 * authorised to access them
	 */
	public static synchronized List<List<String>> unAuthorisedResourcesToTable(
			WebApplication app, Users users) {
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("method");
		row.add("username");
		row.add("password");
        row.add("verifyString");
		table.add(row);
		for (Method method : app.getRestrictedMethods()) {
			for (User user : users.getAllUsersNotInRoles(app
					.getAuthorisedRoles(method.getName()))) {
				row = new ArrayList<String>();
				row.add(method.getName());
				row.add(user.getCredentials().get("username"));
				row.add(user.getCredentials().get("password"));
                row.add(method.getAnnotation(Restricted.class).verifyWithText());
				table.add(row);
			}
		}
		return table;
	}

	public static synchronized void writeTable(String file,
			List<List<String>> table) {
		PrintStream writer = null;
		System.out.println("Writing to table file: "+file);
		try {
			writer = new PrintStream(new FileOutputStream(file, false));
			for (List<String> row : table) {
				StringBuilder sb = new StringBuilder();
				for (String col : row) {
					sb.append("|").append(col).append("\t\t");
				}
				writer.println(sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}

	}
}
