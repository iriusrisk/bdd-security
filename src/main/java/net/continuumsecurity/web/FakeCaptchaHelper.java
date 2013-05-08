package net.continuumsecurity.web;

import net.continuumsecurity.behaviour.ICaptcha;

/*
 * Always fails captcha's
 */
public class FakeCaptchaHelper extends CaptchaFinder implements ICaptchaHelper {

	public FakeCaptchaHelper(Application app) {
		super(app);
	}

	/*
	 * Fails to solve CAPTCHA
	 * (non-Javadoc)
	 * @see net.continuumsecurity.web.ICaptchaHelper#solve()
	 */
	public void solve() {
		
	}

}
