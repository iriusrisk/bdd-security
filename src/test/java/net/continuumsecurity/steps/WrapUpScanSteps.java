package net.continuumsecurity.steps;

import net.continuumsecurity.Config;
import net.continuumsecurity.scanner.ZapManager;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.embedder.Embedder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stephen on 09/02/15.
 */
public class WrapUpScanSteps {
    Logger log = Logger.getLogger(WrapUpScanSteps.class);

    public static final String LATEST_REPORTS = Config.getInstance().getLatestReportsDir();
    public static final String RESOURCES_DIR = "src"+ File.separator+"main"+File.separator+"resources";
    public static final String REPORTS_DIR = Config.getInstance().getReportsDir();

    Embedder embedder;

    /*
        Browsers are re-used between stories. They're all closed by the wrapup method when using the StoryRunner, but the JUnitRunner has to be tricked by using this steps class.
     */
    @AfterStories
    public void afterAllStories() {
        wrapUp();
    }

    public void wrapUp() {
        try {
            ZapManager.getInstance().stopZap();
            copyResultsToStampedReportsDir();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        DriverFactory.quitAll();
    }

    public void setEmbedder(Embedder embedder) {
        this.embedder = embedder;
    }

    private void copyResultsToStampedReportsDir() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss", Locale.getDefault());
        File dirName = new File(REPORTS_DIR+File.separator+formatter.format(new Date()));
        FileUtils.forceMkdir(dirName);
        FileUtils.copyDirectory(new File(LATEST_REPORTS), dirName);
    }
}
