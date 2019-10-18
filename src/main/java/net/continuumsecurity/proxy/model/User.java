package net.continuumsecurity.proxy.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.zaproxy.clientapi.core.ApiResponseSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class User {
    String id;
    boolean enabled;
    String contextId;
    String name;
    Map<String, String> credentials;

    public User(ApiResponseSet apiResponseSet) throws IOException {
        id = apiResponseSet.getStringValue("id");
        enabled = Boolean.valueOf(apiResponseSet.getStringValue("enabled"));
        contextId = apiResponseSet.getStringValue("contextId");
        name = apiResponseSet.getStringValue("name");
        ObjectMapper mapper = new ObjectMapper();
        credentials = mapper.readValue(apiResponseSet.getStringValue("credentials"), new TypeReference<HashMap<String,String>>(){});
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }
}
