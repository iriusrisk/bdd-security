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
package net.continuumsecurity.web.stories;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;
import org.jbehave.core.steps.SilentStepMonitor;

import net.continuumsecurity.web.steps.AutomatedScanningSteps;
import net.continuumsecurity.web.steps.WebApplicationSteps;

public class WebApplicationJUnitStory extends JUnitStory {
/*
	public WebApplicationJUnitStory() {
		PropertyConfigurator.configure("log4j.properties");
		String storyDir = "file:///" + System.getProperty("user.dir")
				+ "/src/main/stories/";
		StoryPathResolver storyPathResolver = new UnderscoredCamelCaseResolver(
				".story");

		Properties viewProperties = new Properties();
		viewProperties.put("decorateNonHtml", "true");
		URL codeLocation = CodeLocations.codeLocationFromURL(storyDir);
		StoryReporterBuilder srb = new StoryReporterBuilder()
				.withDefaultFormats().withViewResources(viewProperties)
				.withFormats(Format.CONSOLE, Format.TXT, Format.HTML)
				.withFailureTrace(false);
		//Setup parameters
		ParameterConverters parameterConverters = new ParameterConverters().addConverters(new ExamplesTableConverter(new ExamplesTableFactory(new LoadFromRelativeFile(codeLocation))));
        // factory to allow parameter conversion and loading from external resources (used by StoryParser too)
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromRelativeFile(codeLocation), parameterConverters);
        // add custom coverters
        parameterConverters.addConverters(new ExamplesTableConverter(examplesTableFactory));
        
		Configuration configuration = new MostUsefulConfiguration()
				.useStoryControls(
						new StoryControls().doDryRun(false)
								.doSkipScenariosAfterFailure(false)
								.doResetStateBeforeStory(true)
								)
				.useStoryLoader(new LoadFromRelativeFile(codeLocation))
				.useStoryReporterBuilder(srb)
				.useStoryPathResolver(storyPathResolver)
				.useStepMonitor(new SilentStepMonitor())
				.useStoryParser(
						new RegexStoryParser(examplesTableFactory))
				.useParameterConverters(parameterConverters);
		configuration.storyControls().useStoryMetaPrefix("story_").useScenarioMetaPrefix("scenario_");		
		
		useConfiguration(configuration);
		addSteps(createSteps(configuration));

		configuredEmbedder().embedderControls()
				.doGenerateViewAfterStories(true)
				.doIgnoreFailureInStories(false)
				.doIgnoreFailureInView(true);
		//configuredEmbedder().useMetaFilters(Arrays.asList("+theme config3"));
	}

	protected List<CandidateSteps> createSteps(Configuration configuration) {
		return new InstanceStepsFactory(configuration,
				new WebApplicationSteps(), new AutomatedScanningSteps())
				.createCandidateSteps();
	}

	*/
}
