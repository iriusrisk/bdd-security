package net.continuumsecurity.web;

import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.caption.CBSolveCaptcha;
import net.continuumsecurity.caption.ISolveCaptcha;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class CaptchaHelper extends CaptchaFinder implements ICaptchaHelper {
	private ISolveCaptcha solver;
	public static Logger log = Logger.getLogger(CaptchaHelper.class);
	
	public CaptchaHelper(ICaptcha app) {
		super(app);
		solver = new CBSolveCaptcha();
	}

	public void solve() {
		WebElement img = null;
		String solved = null;
		try {
			img = app.getCaptchaImage();
			solved = solver.solveFromUrl(img.getAttribute("src"));
		} catch (NoSuchElementException nse) {
			log.debug("No CAPTCHA found, skipping.");
			return;
		}
		if (solved != null) {
			app.getCaptchaResponseField().sendKeys(solved);
		}
	}
	

}
