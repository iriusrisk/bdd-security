package net.continuumsecurity.web;

import net.continuumsecurity.behaviour.ICaptcha;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class CaptchaFinder {
	protected ICaptcha app;
	
	public CaptchaFinder(ICaptcha app) {
		this.app = app;
	}
	
	public boolean isPresent() {
		WebElement img = null;
		try {
			img = app.getCaptchaImage();
			if (img != null) return true;
		} catch (NoSuchElementException nse) {
			
		}
		return false;
	}
}
