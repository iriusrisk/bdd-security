package net.continuumsecurity.clients;


import java.util.Map;

/**
 * Methods to get, set and delete the authentication tokens used by the HTTP client.
 */
public interface AuthTokenManager {
    Map<String,String> getAuthTokens();
    void setAuthTokens(Map<String,String> tokens);
    void deleteAuthTokens();
}
