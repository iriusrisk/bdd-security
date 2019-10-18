package net.continuumsecurity.proxy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Authentication methods supported by ZAP.
 */
public enum AuthenticationMethod {
    FORM_BASED_AUTHENTICATION("formBasedAuthentication"),
    HTTP_AUTHENTICATION("httpAuthentication"),
    MANUAL_AUTHENTICATION("manualAuthentication"),
    SCRIPT_BASED_AUTHENTICATION("scriptBasedAuthentication");

    private String value;

    public String getValue() {
        return value;
    }

    AuthenticationMethod(String authenticationMethod) {
        this.value = authenticationMethod;
    }

    public static List<String> getValues() {
        List<String> values = new ArrayList<String>();
        for (AuthenticationMethod authenticationMethod : AuthenticationMethod.values()) {
            values.add(authenticationMethod.getValue());
        }
        return values;
    }
}
