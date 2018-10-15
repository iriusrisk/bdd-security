package net.continuumsecurity;

import com.rits.cloning.Cloner;
import difflib.DiffUtils;
import difflib.Patch;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarHeader;
import edu.umass.cs.benchlab.har.HarRequest;
import edu.umass.cs.benchlab.har.HarResponse;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static Logger log = Logger.getLogger(Utils.class);

    public static String extractSessionIDName(String target) {
        if (Config.getInstance().getSessionIDs().size() == 0) {
            log.warn("Attempting to extract session ID from string, but no session IDs defined in the configuration.");
        }
        for (String sessId : Config.getInstance().getSessionIDs()) {
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

    public static boolean responseContainsHeader(HarResponse response, String headerName) {
        for (HarHeader header : response.getHeaders().getHeaders()) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }

    public static String getResponseHeaderValue(HarResponse response, String headerName) {
        for (HarHeader header : response.getHeaders().getHeaders()) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                return header.getValue();
            }
        }
        return null;
    }

    public static boolean responseHeaderValueIsOneOf(HarResponse response, String headerName,String[] permittedValues) {
        for (HarHeader header : response.getHeaders().getHeaders()) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                for (String permitted : permittedValues) {
                    if (permitted.equalsIgnoreCase(header.getValue())) return true;
                }
            }
        }
        return false;
    }

    public static boolean mapOfStringListContainsString(Map<String, List<String>> map, String target) {
        log.info("Searching ciphers for: "+target);
        for (List<String> list : map.values()) {
            for (String value : list) {
                log.info(value);
                if (value.contains(target)) return true;
            }
        }
        return false;
    }
    
    public static List<String> createListOfValues(String pathToTable) {
        BufferedReader br = null;
        List<String> ls = new ArrayList<String>();
        try {
          br = new BufferedReader(new FileReader(pathToTable));
          String line = br.readLine();
          while (line != null) {
            line = line.replace("|","");
            ls.add(line.trim());
            line = br.readLine();
          }
        }catch (Exception e){
          e.printStackTrace();
        } finally {
          try{
            if (br != null){
              br.close();
            }
          }catch(Exception e){
            e.printStackTrace();
          }
        }
        return ls;
      }
      
      public static List<HashMap> createListOfMaps(String pathToTable) {
        BufferedReader br = null;
        List<HashMap> listMap = new ArrayList<HashMap>();
        try {
          br = new BufferedReader(new FileReader(pathToTable));
          String line = br.readLine();
          String[] firstLine = line.split("\\|");
          line = br.readLine();
          while (line != null) {
            String[] lineList = line.split("\\|");
            HashMap<String, String> map = new HashMap<String, String>();
            int i = 0;
            for(String item: lineList){
              map.put(firstLine[i].trim(),item.trim());
              i = i + 1;
            }
            listMap.add(map);
            line = br.readLine();
          }
        }catch (Exception e){
          e.printStackTrace();
        } finally {
          try{
            if (br != null){
              br.close();
            }
          }catch(Exception e){
            e.printStackTrace();
          }
        }
        return listMap;
      }

      public static String createStringFromJBehaveTable(String pathToTable){
        String jbehaveTable;
        BufferedReader br = null;
        try {
          br = new BufferedReader(new FileReader(pathToTable));
          StringBuilder sb = new StringBuilder();
          String line = br.readLine();
          while (line != null) {
              sb.append(line);
              sb.append("\n");
              line = br.readLine();
          }
          jbehaveTable = sb.toString();
        }catch (Exception e){
          jbehaveTable = "";
          e.printStackTrace();
        } finally {
          try{
            if (br != null){
              br.close();
            }
          }catch(Exception e){
            e.printStackTrace();
          }
        }
        return jbehaveTable;
      }

    public static String getHostFromUrl(String url) throws MalformedURLException {
        URL theUrl = new URL(url);
        return theUrl.getHost();
    }

    public static int getPortFromUrl(String url) throws MalformedURLException {
        URL theUrl = new URL(url);
        return theUrl.getPort();
    }

}
