package net.continuumsecurity.junit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import net.continuumsecurity.scanner.ZapManager;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.masterthought.cucumber.ReportBuilder;

public class SecurityTestRunner {
	private static Logger log = Logger.getLogger(SecurityTestRunner.class.getName());
	private static final String BUILD_NUMBER = "bdd-security-1.0";
	private static final String PROJECT_NAME = "BDD-Security";
	private static final String REPORT_DIRECTORY = "build/reports";
	private static final String[] ARGUMENTS = { "--plugin", "pretty", "--glue", "net.continuumsecurity.steps",
			"classpath:features", "--plugin", "json:build/reports/index.json" };

	public static void main(String[] args) throws Throwable {

		String reportPrettyDirectory = REPORT_DIRECTORY + "/cucumber/pretty";

		System.out.println("Cleaning existing reports if exist.");
		cleanup();

		System.out.println("Started test execution.");
		byte status = executeTests();
		System.out.println("Test execution completed with status " + status);

		System.out.println("Started report generation.");
		generatePrettyHTMLReports(reportPrettyDirectory);
		System.out.println("Report generation completed.");

	}

	/**
	 * Clean up of reports directory if presents.
	 * 
	 * @throws IOException
	 */
	public static void cleanup() throws IOException {
		if (new File(REPORT_DIRECTORY).exists()) {
			Files.walk(Paths.get(REPORT_DIRECTORY)).sorted(Comparator.reverseOrder()).map(Path::toFile)
					.forEach(File::delete);
		}
	}

	/**
	 * Execute cucumber features.
	 */
	public static byte executeTests() {
		try {

			byte status = cucumber.api.cli.Main.run(ARGUMENTS, Thread.currentThread().getContextClassLoader());
			System.out.println("Exit Status of cucumber execution : " + status);
			return status;
		} catch (Exception exp) {
			log.error("Error while executing main runnuer class." + exp);
			return 1;
		} finally {
			System.out.println("Closing all browsers.");
			tearDown();
		}
	}

	/**
	 * Generate pretty html report using cucumber generated json files.
	 * 
	 * @param reportDirectory
	 */
	public static void generatePrettyHTMLReports(String reportDirectory) {

		File dir = new File(REPORT_DIRECTORY);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});

		List<String> jsonReportFiles = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			jsonReportFiles.add(REPORT_DIRECTORY + "/" + files[i].getName());
		}

		boolean skippedFails = false;
		boolean pendingFails = false;
		boolean undefinedFails = true;
		boolean missingFails = true;
		boolean runWithJenkins = false;
		boolean parallelTesting = false;

		net.masterthought.cucumber.Configuration configuration = new net.masterthought.cucumber.Configuration(
				new File(reportDirectory), PROJECT_NAME);
		configuration.setStatusFlags(skippedFails, pendingFails, undefinedFails, missingFails);
		configuration.setParallelTesting(parallelTesting);
		configuration.setRunWithJenkins(runWithJenkins);
		configuration.setBuildNumber(BUILD_NUMBER);
		ReportBuilder reportBuilder = new ReportBuilder(jsonReportFiles, configuration);
		reportBuilder.generateReports();

	}

	public static void tearDown() {
		DriverFactory.quitAll();
		ZapManager.getInstance().stopZap();
	}
}
