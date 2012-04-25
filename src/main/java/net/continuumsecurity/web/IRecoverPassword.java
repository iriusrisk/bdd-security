package net.continuumsecurity.web;

import java.util.Map;

public interface IRecoverPassword {
	void submitRecover(Map<String,String> details);
}
