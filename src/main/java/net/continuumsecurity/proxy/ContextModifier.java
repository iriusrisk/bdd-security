package net.continuumsecurity.proxy;

public interface ContextModifier {
    void setIncludeInContext(String contextName, String regex);
}
