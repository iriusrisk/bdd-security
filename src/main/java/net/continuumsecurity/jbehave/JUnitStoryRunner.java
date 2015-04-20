package net.continuumsecurity.jbehave;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import net.continuumsecurity.steps.*;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by stephen on 09/02/15.
 */
@RunWith(JUnitReportingRunner.class)
public class JUnitStoryRunner extends BaseStoryRunner {

    public JUnitStoryRunner() {
        super();
        List<String> filters = createFilters();
        String filter = System.getProperty("filters");
        String stories = System.getProperty("stories");
        if (stories != null) {
            filters.addAll(createStoryMetaFilters(stories));
        } else if (filter != null && !filter.contains("groovy:")) {
            filters.add("-skip");
        }
        filters.add(filter);
        configuredEmbedder().useMetaFilters(filters);
        configuredEmbedder().generateReportsView();
        JUnitReportingRunner.recommandedControls(configuredEmbedder());
        try {
            prepareReportsDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Collection<? extends String> createStoryMetaFilters(String stories) {
        String[] storyArray = stories.split(",");
        StringBuilder groovyMatcher = new StringBuilder("groovy: (");
        Iterator i = Arrays.asList(storyArray).iterator();
        while (i.hasNext()) {
            groovyMatcher.append("story == '").append(i.next()).append("'");
            if (i.hasNext()) groovyMatcher.append(" || ");
        }
        groovyMatcher.append(") && skip == false");
        return Arrays.asList(groovyMatcher.toString());
    }

    protected void prepareReportsDir() throws IOException {
        FileUtils.deleteQuietly(new File(WrapUpScanSteps.LATEST_REPORTS));
        //FileUtils.cleanDirectory(new File(WrapUpScanSteps.JUNIT_REPORTS_DIR));
        FileUtils.forceMkdir(new File(WrapUpScanSteps.LATEST_REPORTS+File.separator+"zap"));

        File viewDir = new File(WrapUpScanSteps.LATEST_REPORTS + File.separator+"view");
        FileUtils.copyDirectory(new File(WrapUpScanSteps.RESOURCES_DIR), viewDir);
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(),
                new WebApplicationSteps(),
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
