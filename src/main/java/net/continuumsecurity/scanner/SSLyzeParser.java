package net.continuumsecurity.scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by stephen on 18/01/15.
 */
public class SSLyzeParser {
    String output;
    final static String NEWLINE = "\r\n|[\n\r\u2028\u2029\u0085]";

    public SSLyzeParser(String output) {
        this.output = output;
    }

    public List<CipherElement> listPreferredCipherSuitesFor(String protocol) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);

        while (lineScanner.hasNext()) {
            if (lineScanner.next().contains(protocol + " Cipher Suites")) break;
        }
        lineScanner.next();
        List<CipherElement> found = new ArrayList<>();
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.contains("Accepted") || line.length() == 0) {
                break;
            } else {
                Scanner wordScanner = new Scanner(line);
                String name = wordScanner.next();
                wordScanner.next();
                wordScanner.next();
                int size = wordScanner.nextInt();
                found.add(new CipherElement(name,size));
                wordScanner.close();
            }
        }
        lineScanner.close();
        return found;
    }

    public List<String> listPreferredCipherSuiteNamesFor(String protocol) {
        List<String> names = new ArrayList<>();
        for (CipherElement element : listPreferredCipherSuitesFor(protocol)) {
            names.add(element.getName());
        }
        return names;
    }

    public List<String> listAcceptedCipherSuiteNamesFor(String protocol) {
        List<String> names = new ArrayList<>();
        for (CipherElement element : listAcceptedCipherSuitesFor(protocol)) {
            names.add(element.getName());
        }
        return names;
    }

    public List<String> listAllSupportedProtocols() {
        List<String> all = new ArrayList<>();
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);
        String line;
        while (lineScanner.hasNext()) {
            line = lineScanner.next();
            if (line.contains("Cipher Suites:")) {
                String nextLine = lineScanner.next();
                if (!nextLine.contains("rejected")) {
                    Scanner wordScanner = new Scanner(line);
                    wordScanner.next();
                    all.add(wordScanner.next());
                    wordScanner.close();
                }
            }
        }
        lineScanner.close();
        return all;
    }

    public List<String> listAllAcceptedCiphers() {
        List<String> all = new ArrayList<>();
        for (String protocol : listAllSupportedProtocols()) {
            all.addAll(listAcceptedCipherSuiteNamesFor(protocol));
        }
        return all;
    }

    public int findSmallestAcceptedKeySize() {
        List<Integer> all = new ArrayList<>();
        for (String protocol : listAllSupportedProtocols()) {
            all.add(findSmallestAcceptedKeySize(protocol));
        }
        Collections.sort(all);
        if (all.size() == 0) throw new RuntimeException("No keys found.");
        return all.get(0);
    }

    public int findSmallestAcceptedKeySize(String protocol) {
        List<Integer> all = new ArrayList<>();
        for (CipherElement cipherElement : listAcceptedCipherSuitesFor(protocol)) {
            all.add(cipherElement.getSize());
        }
        Collections.sort(all);
        if (all.size() == 0) throw new RuntimeException("No keys found for protocol "+protocol);
        return all.get(0);
    }

    public List<CipherElement> listAcceptedCipherSuitesFor(String protocol) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);

        while (lineScanner.hasNext()) {
            if (lineScanner.next().contains(protocol + " Cipher Suites")) break;
        }
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.contains("Accepted") || line.contains("Server rejected")) break;
        }
        List<CipherElement> found = new ArrayList<>();
        while (lineScanner.hasNext()) {
            String line = lineScanner.next();
            if (line.length() == 0 || line.contains("Undefined")) {
                break;
            } else {
                Scanner wordScanner = new Scanner(line);
                String name = wordScanner.next();
                String code = wordScanner.next();
                if (!code.matches("-")) wordScanner.next();
                int size = wordScanner.nextInt();
                found.add(new CipherElement(name,size));
                wordScanner.close();
            }
        }
        lineScanner.close();
        return found;
    }

    public boolean containsCipherWithPartialName(String name) {
        for (String cipher : listAllAcceptedCiphers()) {
            if (cipher.contains(name)) return true;
        }
        return false;
    }

    public boolean supportsCipher(String name) {
        for (String cipher : listAllAcceptedCiphers()) {
            if (cipher.equals(name)) return true;
        }
        return false;
    }

    public boolean doesAnyLineMatch(String regex) {
        Scanner lineScanner = new Scanner(output).useDelimiter(NEWLINE);
        while (lineScanner.hasNext()) {
            if (lineScanner.next().matches(regex)) return true;
        }
        return false;
    }


    private class CipherElement {
        String name;
        int size;

        public CipherElement(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
