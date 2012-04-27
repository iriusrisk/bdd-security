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

import java.net.URL;
import java.util.Properties;

import net.continuumsecurity.Config;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.SilentStepMonitor;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;

public class PreferredConfiguration extends MostUsefulConfiguration {
	
	public PreferredConfiguration(String storyUrl) {
		URL codeLocation = CodeLocations.codeLocationFromURL(storyUrl);
		Properties viewResources = new Properties();
		StoryPathResolver storyPathResolver = new UnderscoredCamelCaseResolver(
				".story");
		StoryReporterBuilder srb = new StoryReporterBuilder()
				.withDefaultFormats()
				.withViewResources(viewResources)
				.withFormats(Format.XML, Format.CONSOLE, Format.HTML, Format.IDE_CONSOLE,Format.TXT)
				.withFailureTrace(Config.displayStackTrace());

		// Setup parameters
		ParameterConverters parameterConverters = new ParameterConverters()
				.addConverters(new ExamplesTableConverter(
						new ExamplesTableFactory(new LoadFromRelativeFile(
								codeLocation))));
		// factory to allow parameter conversion and loading from external
		// resources (used by StoryParser too)
		ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(
				new LocalizedKeywords(),
				new LoadFromRelativeFile(codeLocation), parameterConverters);
		// add custom coverters
		parameterConverters.addConverters(new ExamplesTableConverter(
				examplesTableFactory));
		viewResources.put("decorateNonHtml", "true");
		useStoryControls(
				new StoryControls().doDryRun(false)
						.doSkipScenariosAfterFailure(false)
						.doResetStateBeforeStory(true)
						.useStoryMetaPrefix("story_")
						.useScenarioMetaPrefix("scenario_"))
				.useStoryLoader(new LoadFromRelativeFile(codeLocation))
				.useStoryReporterBuilder(srb)
				.useStoryPathResolver(storyPathResolver)
				.useStepMonitor(new SilentStepMonitor())
				.useStoryParser(new RegexStoryParser(examplesTableFactory))
				.useParameterConverters(parameterConverters);
	}
}
