package net.continuumsecurity.behaviour;

import net.continuumsecurity.web.ICaptchaHelper;
import org.openqa.selenium.WebElement;

public interface ICaptcha {
	WebElement getCaptchaImage();
	WebElement getCaptchaResponseField();
    public void setCaptchaHelper(ICaptchaHelper helper);
}
