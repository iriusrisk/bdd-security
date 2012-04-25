package net.continuumsecurity.behaviour;

import org.openqa.selenium.WebElement;

public interface ICaptcha {
	WebElement getCaptchaImage();
	WebElement getCaptchaResponseField();
}
