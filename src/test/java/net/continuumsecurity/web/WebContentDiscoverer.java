package net.continuumsecurity.web;

import net.continuumsecurity.clients.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebContentDiscoverer implements IContentDiscoverer {
    Browser browser;

    public WebContentDiscoverer(Browser browser) {
        this.browser = browser;
    }

    @Override
    public WebElement findUsernameField() {
        return null;
    }

    @Override
    public WebElement findPasswordField() {
        browser.getWebDriver().findElement(By.ByClassName)
    }
}
