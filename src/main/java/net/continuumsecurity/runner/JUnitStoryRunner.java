package net.continuumsecurity.runner;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import net.continuumsecurity.web.steps.*;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 09/02/15.
 */
@RunWith(JUnitReportingRunner.class)
public class JUnitStoryRunner extends BaseStoryRunner {

    public JUnitStoryRunner() {
        super();
        List<String> filters = createFilters();
        filters.add("-skip");
        configuredEmbedder().useMetaFilters(filters);
        JUnitReportingRunner.recommandedControls(configuredEmbedder());
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        WebApplicationSteps ws = new WebApplicationSteps();

        return new InstanceStepsFactory(configuration(),
                ws,
                new InfrastructureSteps(),
                new NessusScanningSteps(),
                new SSLyzeSteps(),
                new AppScanningSteps(),
                new CloseAllBrowsersSteps());
    }

    public List<String> createFilters() {
        return new ArrayList<>();
    }

    @Override
    public List<String> storyPaths() {
        List<String> includes = new ArrayList<String>();
        includes.add("**/*.story");

        List<String> excludes = new ArrayList<String>();
        excludes.add("**/configuration.story");
        excludes.add("**/navigate_app.story");
        return new StoryFinder().findPaths(
                CodeLocations.codeLocationFromURL(storyUrl), includes,
                excludes);
    }

}
