package net.continuumsecurity.web;

import org.openqa.selenium.WebElement;

public interface IContentDiscoverer {
    WebElement findUsernameField();
    WebElement findPasswordField();
}
