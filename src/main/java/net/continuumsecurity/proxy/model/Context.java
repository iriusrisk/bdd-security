package net.continuumsecurity.proxy.model;

import org.zaproxy.clientapi.core.ApiResponseSet;

import java.util.Arrays;
import java.util.List;

public class Context {
    private String id;
    private String name;
    private String description;
    private String loggedInPattern;
    private String loggedOutPattern;
    private List<String> includedRegexs;
    private List<String> excludedRegexs;
    private String authType;
    private int authenticationDetectionMethodId;

    public Context(ApiResponseSet response) {
        id = response.getStringValue("id");
        name = response.getStringValue("name");
        description = response.getStringValue("description");
        loggedInPattern = response.getStringValue("loggedInPattern");
        loggedOutPattern = response.getStringValue("loggedOutPattern");
        String includedRegexsNode = response.getStringValue("includeRegexs");
        if (includedRegexsNode.length() > 2) {
            includedRegexs = Arrays.asList(includedRegexsNode.substring(1, includedRegexsNode.length()-1).split(", "));
        }
        String excludedRegexsNode = response.getStringValue("excludeRegexs");
        if (excludedRegexsNode.length() > 2) {
            excludedRegexs = Arrays.asList(excludedRegexsNode.substring(1, excludedRegexsNode.length()-1).split(", "));
        }
        authType = response.getStringValue("authType");
        authenticationDetectionMethodId = Integer.parseInt(response.getStringValue("authenticationDetectionMethodId"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLoggedInPattern() {
        return loggedInPattern;
    }

    public void setLoggedInPattern(String loggedInPattern) {
        this.loggedInPattern = loggedInPattern;
    }

    public String getLoggedOutPattern() {
        return loggedOutPattern;
    }

    public void setLoggedOutPattern(String loggedOutPattern) {
        this.loggedOutPattern = loggedOutPattern;
    }

    public List<String> getIncludedRegexs() {
        return includedRegexs;
    }

    public void setIncludedRegexs(List<String> includedRegexs) {
        this.includedRegexs = includedRegexs;
    }

    public List<String> getExcludedRegexs() {
        return excludedRegexs;
    }

    public void setExcludedRegexs(List<String> excludedRegexs) {
        this.excludedRegexs = excludedRegexs;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public int getAuthenticationDetectionMethodId() {
        return authenticationDetectionMethodId;
    }

    public void setAuthenticationDetectionMethodId(int authenticationDetectionMethodId) {
        this.authenticationDetectionMethodId = authenticationDetectionMethodId;
    }
}
