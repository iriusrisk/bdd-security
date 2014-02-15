package net.continuumsecurity.web;

/*
 * Always fails captcha's
 */
public class FakeCaptchaSolver extends CaptchaFinder implements ICaptchaSolver {

	public FakeCaptchaSolver(Application app) {
		super(app);
	}

	/*
	 * Fails to solve CAPTCHA
	 * (non-Javadoc)
	 * @see net.continuumsecurity.web.ICaptchaSolver#solve()
	 */
	public void solve() {
		
	}

}
