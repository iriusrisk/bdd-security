package net.continuumsecurity.web.steps;

import net.continuumsecurity.web.drivers.DriverFactory;
import org.jbehave.core.annotations.AfterStories;

/**
 * Created by stephen on 09/02/15.
 */
public class CloseAllBrowsersSteps {

    /*
        Browsers are re-used between stories. They're all closed by the wrapup method when using the StoryRunner, but the JUnitRunner has to be tricked by using this steps class.
     */
    @AfterStories
    public void closeBrowsers() {
        DriverFactory.quitAll();
    }
}
