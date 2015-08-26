package net.continuumsecurity.behaviour;

public interface ICors {
	void makeCorsRequest(String path, String origin);
	String getAccessControlAllowOriginHeader();
}
