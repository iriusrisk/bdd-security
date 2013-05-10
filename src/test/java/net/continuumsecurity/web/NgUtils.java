package net.continuumsecurity.web;

import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

public class NgUtils {
  
  public static String createStringFromJBehaveTable(String pathToTable){
    String jbehaveTable;
    BufferedReader br;
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
          br.close();
        }catch(Exception e){
          e.printStackTrace();
        }
    }
    return jbehaveTable;
  }
}
