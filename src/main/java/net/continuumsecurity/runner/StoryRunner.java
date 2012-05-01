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
package net.continuumsecurity.runner;

import net.continuumsecurity.Config;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.BurpFactory;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import net.continuumsecurity.web.steps.WebApplicationSteps;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class StoryRunner extends BaseStoryRunner {
	final CmdLineParser parser;
	private static final String LATEST_REPORTS = Config.getLatestReportsDir();
	private static final String RESOURCES_DIR = "src"+File.separator+"main"+File.separator+"resources";
	private static final String REPORTS_DIR = Config.getReportsDir();

	@Option(name = "-m", usage = "append meta filters")
	private String useFilters;

	@Option(name = "-s")
	private boolean skipConfigurationStories = false;
	
	@Option(name = "-c")
	private boolean justRunConfig = false;

	@Option(name = "-h")
	private boolean help = false;

	public StoryRunner() {
		super();
		// configuredEmbedder().useEmbedderControls(new
		// PropertyBasedEmbedderControls());
		
		parser = new CmdLineParser(this);
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		WebApplicationSteps ws = new WebApplicationSteps();
		return new InstanceStepsFactory(configuration(),
				ws,
				new AutomatedScanningSteps());
	}

	@Override
	public List<String> storyPaths() {
		return new StoryFinder().findPaths(
				CodeLocations.codeLocationFromURL(storyUrl), "**/*.story",
				"**/configuration_story.story");
	}
	
	private List<String> parseMetaFilters() {
		return Arrays.asList(useFilters.split("\\,"));
	}
	
	private void prepareReportsDir() throws IOException {
		FileUtils.deleteQuietly(new File(LATEST_REPORTS));
		File viewDir = new File(LATEST_REPORTS + File.separator+"view");
		FileUtils.copyDirectory(new File(RESOURCES_DIR), viewDir);
	}
	
	private void copyResultsToStampedReportsDir() throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss", Locale.getDefault());
		File dirName = new File(REPORTS_DIR+File.separator+formatter.format(new Date()));
		FileUtils.forceMkdir(dirName);
		FileUtils.copyDirectory(new File(LATEST_REPORTS), dirName);
	}

	/*
	 * Add required meta filters to control which stories are run
	 */
    protected List<String> createFilters() {
		List<String> filters = new ArrayList<String>();
		if (useFilters != null)
			filters.addAll(parseMetaFilters());
		
		WebApplication app = Config.createApp(null);
		if (app instanceof ILogin) {
			log.debug(" app implements ILogin");
		} else {
			log.debug(" app doesn't implement ILogin, skipping authentication tests");
			filters.add("-story Authentication");
		}
		filters.add("-skip");
		log.debug(" running with filters:");
		for (String filter : filters) {
			log.debug("\t"+filter);
		}
		return filters;
	}
	
	public void execute(String... argv) throws CmdLineException,IOException {
		parser.parseArgument(argv);
		
		if (help) {
			parser.setUsageWidth(Integer.MAX_VALUE);
			parser.printUsage(System.err);
			System.exit(0);
		}
		prepareReportsDir();
		List<String> filters = createFilters();
		configuredEmbedder().useMetaFilters(filters);
		
		if (!skipConfigurationStories) {
			try {
				log.debug("Running configuration stories");
				ConfigurationStoryRunner configRunner = new ConfigurationStoryRunner(filters);
				configRunner.run();
				log.debug("Configuration stories completed.");
			} catch (Throwable t) {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				log.error("Configuration stories failed: " + t.getMessage());
				log.error("Halting execution");
				log.error(sw.toString());
				t.printStackTrace();
				wrapUp();
				System.exit(1);
			}
		}
		if (justRunConfig) {
			wrapUp();
			System.exit(0);
		}
		
		try {	
			run();
			log.debug("Completed StoryRunner.run() successfully.");
		} catch (Throwable e) {
			log.debug("Caught exception from StoryRunner.run()");
			e.printStackTrace();
		} finally {
			
			wrapUp();
		}
		System.exit(0);
	}
	
	public void wrapUp() {
		configuredEmbedder().generateReportsView();
		try {
			copyResultsToStampedReportsDir();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		BurpFactory.destroyAll();
		DriverFactory.quitAll();
	}

	public static void main(String... argv) throws CmdLineException,IOException {
		StoryRunner storyRunner = new StoryRunner();
		storyRunner.execute(argv);
	}
}
