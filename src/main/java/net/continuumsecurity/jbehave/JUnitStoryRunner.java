package net.continuumsecurity.jbehave;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import net.continuumsecurity.steps.*;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
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
        String filter = System.getProperty("filters");
        if (filter == null) filter = "-skip";
        filters.add(filter);
        try {
            prepareReportsDir();
        } catch (IOException e) {
            System.err.println("Error preparing reports directory");
            e.printStackTrace();
            System.exit(1);
        }
        configuredEmbedder().useMetaFilters(filters);
        configuredEmbedder().generateReportsView();
        JUnitReportingRunner.recommandedControls(configuredEmbedder());
    }

    protected void prepareReportsDir() throws IOException {
        FileUtils.deleteQuietly(new File(WrapUpScanSteps.LATEST_REPORTS));
        //FileUtils.cleanDirectory(new File(WrapUpScanSteps.JUNIT_REPORTS_DIR));
        File viewDir = new File(WrapUpScanSteps.LATEST_REPORTS + File.separator+"view");
        FileUtils.copyDirectory(new File(WrapUpScanSteps.RESOURCES_DIR), viewDir);
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
                new WrapUpScanSteps()
                );
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
