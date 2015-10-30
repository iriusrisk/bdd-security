package net.continuumsecurity.behaviour;

import java.util.Map;

public interface IRecoverPassword {
	void submitRecover(Map<String,String> details);
}
