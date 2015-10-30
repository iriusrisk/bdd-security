package net.continuumsecurity.web;


public interface ICaptchaSolver {
	public void solve();
	public boolean isCaptchaPresent();
}
