package net.continuumsecurity;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by stephen on 16/01/15.
 */
public class ProcessExecutor {
    ProcessBuilder pb;
    Process process;
    File outputFile;
    String filename;

    public ProcessExecutor(String... cmds) {
        pb = new ProcessBuilder(cmds).inheritIO();
    }

    public ProcessExecutor(List<String> cmds) {
        pb = new ProcessBuilder(cmds).inheritIO();
    }

    public void setFilename(String filename) throws IOException {
        this.filename = filename;
        outputFile = new File(filename);
    }

    private void createNewOutputFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (Files.exists(path)) Files.delete(path);
        outputFile.createNewFile();
    }

    public void start() throws IOException {
        if (outputFile == null) createNewOutputFile(filename);
        pb.redirectOutput(outputFile);
        process = pb.start();
        int exitValue = 0;
        String line;
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        try {
            exitValue = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitValue != 0) {
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            throw new RuntimeException("Error. Process ended with exit code: "+exitValue);
        }
    }

    public String getOutput() throws IOException {
        return FileUtils.readFileToString(outputFile);
    }

}
