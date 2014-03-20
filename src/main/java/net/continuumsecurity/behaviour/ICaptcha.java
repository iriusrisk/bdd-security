package net.continuumsecurity.behaviour;

import net.continuumsecurity.web.ICaptchaSolver;

import org.openqa.selenium.WebElement;

public interface ICaptcha {
	WebElement getCaptchaImage();
	WebElement getCaptchaResponseField();
	public void setCaptchaSolver(ICaptchaSolver solver);
    public void setDefaultSolver();
}
