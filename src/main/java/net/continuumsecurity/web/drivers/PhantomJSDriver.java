package net.continuumsecurity.web.drivers;

import java.io.IOException;

public class PhantomJSDriver {
	public PhantomJSDriver(String path, String host, int port) throws IOException {
		Runtime.getRuntime().exec(path);
	}
}
