package net.continuumsecurity.web;

import java.io.File;
import java.io.BufferReader;
import java.lang.StringBuilder;

public class NgUtils {
  
  public static String createStringFromJBehaveTable(File pathToTable){
    BufferedReader br = new BufferedReader(new FileReader(pathToTable));
    String jbehaveTable;
    try {
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
    } finally {
        br.close();
    }
    return jbehaveTable;
  }
}
