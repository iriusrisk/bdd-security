package net.continuumsecurity.web;

import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;

public class NgUtils {
 
  public static List<String> createListOfValues(String pathToTable) {
    BufferedReader br = null;
    List<String> ls = new ArrayList<String>();
    try {
      br = new BufferedReader(new FileReader(pathToTable));
      String line = br.readLine();
      while (line != null) {
        line = line.replace("|","");
        ls.add(line);
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
}
