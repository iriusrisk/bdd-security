package net.continuumsecurity.web;

import org.openqa.selenium.WebElement;

public interface ICaptcha {
	WebElement getCaptchaImage();
	WebElement getCaptchaResponseField();
}
