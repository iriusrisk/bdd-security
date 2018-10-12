package net.continuumsecurity.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import net.continuumsecurity.scanner.ZapManager;
import net.continuumsecurity.web.drivers.DriverFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.masterthought.cucumber.ReportBuilder;

public class SecurityTestRunner {
	public static void main(String[] args) throws Throwable {
		
		String report_dir = "build/reports";
		String report_pretty_dir = report_dir + "/cucumber/pretty"; 
		
		if(args[0].equals("tests")) {
			if(new File(report_dir).exists()) {
				Files.walk(Paths.get(report_dir)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			// Test execution starts here
		    String[] args1 = {"--plugin", "pretty", "--glue", "net.continuumsecurity.steps", "classpath:features" , "--plugin" ,"json:reports/index.json"};
			cucumber.api.cli.Main.main(args1);
		}
		
		if(args[0].equals("reports")) {
			
			File dir = new File(report_dir);
			File[] files = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".json");
			    }
			});
			
			List<String> jsonReportFiles = new ArrayList<String>();
			for (int i = 0; i < files.length; i++) {
				jsonReportFiles.add(report_dir + "/" + files[i].getName());
			}
			
		    String projectName = "BDD-Security";
		    boolean skippedFails = false;
		    boolean pendingFails = false;
		    boolean undefinedFails = true;
		    boolean missingFails = true;
		    boolean runWithJenkins = false;
		    boolean parallelTesting = false;
	
		    net.masterthought.cucumber.Configuration configuration = new net.masterthought.cucumber.Configuration(new File(report_pretty_dir), projectName);
		    configuration.setStatusFlags(skippedFails, pendingFails, undefinedFails, missingFails);
		    configuration.setParallelTesting(parallelTesting);
		    configuration.setRunWithJenkins(runWithJenkins);
		    configuration.setBuildNumber("SecTests.1");
		    ReportBuilder reportBuilder = new ReportBuilder(jsonReportFiles, configuration);
		    reportBuilder.generateReports();
		    System.out.println("\nReport available..");
		} 
	}
}

