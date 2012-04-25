package net.continuumsecurity.web;

import net.continuumsecurity.caption.CBSolveCaptcha;
import net.continuumsecurity.caption.ISolveCaptcha;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class Captcha {
	private static Captcha instance;
	private ISolveCaptcha solver;
	public static Logger log = Logger.getLogger(Captcha.class);

	private Captcha() {
		solver = new CBSolveCaptcha();
	}

	public static Captcha instance() {
		if (instance == null)
			instance = new Captcha();
		return instance;
	}

	public synchronized void solve(ICaptcha app) {
		WebElement img = null;
		String solved = null;
		try {
			img = app.getCaptchaImage();
			solved = solver.solveFromUrl(img.getAttribute("src"));
		} catch (NoSuchElementException nse) {
			log.info("No CAPTCHA found");
			return;
		}
		if (solved != null) {
			app.getCaptchaResponseField().sendKeys(solved);
		}
	}
	
	public synchronized boolean isPresent(ICaptcha app) {
		WebElement img = null;
		try {
			img = app.getCaptchaImage();
			if (img != null) return true;
		} catch (NoSuchElementException nse) {
			
		}
		return false;
	}
}
