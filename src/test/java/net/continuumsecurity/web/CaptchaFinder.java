package net.continuumsecurity.web;

import net.continuumsecurity.behaviour.ICaptcha;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class CaptchaFinder {
    Logger log = Logger.getLogger(CaptchaFinder.class);

	protected Application app;
	
	public CaptchaFinder(Application app) {
		this.app = app;
	}
	
	public boolean isCaptchaPresent() {
		WebElement img = null;
		try {
			img = ((ICaptcha)app).getCaptchaImage();
			if (img != null) {
				log.debug("Found CAPTCHA on element: "+img.toString());
				return true;
			}
		} catch (NoSuchElementException nse) {
			
		}
		log.debug("No CAPTCHA found");
		return false;
	}
}
