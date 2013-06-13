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
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.steps.InjectableStepsFactory;

import java.util.List;

public abstract class BaseStoryRunner extends JUnitStories {
	Logger log = Logger.getLogger(StoryRunner.class);
	protected static String storyUrl = Config.getStoryUrl();
	
	public BaseStoryRunner() {
		PropertyConfigurator.configure("log4j.properties");
		configuredEmbedder().embedderControls()
		.doGenerateViewAfterStories(false) //We'll generate it manually after the stories are done
		.doIgnoreFailureInStories(true)
		.doIgnoreFailureInView(true)
		.useStoryTimeoutInSecs(30*60);
	}
	
	@Override
	public Configuration configuration() {
		return new PreferredConfiguration(storyUrl);
	}
	
	@Override
	public abstract List<String> storyPaths();

	public abstract InjectableStepsFactory stepsFactory();
	
	
}
