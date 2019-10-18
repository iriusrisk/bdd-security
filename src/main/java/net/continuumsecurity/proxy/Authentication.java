package net.continuumsecurity.proxy;

import net.continuumsecurity.proxy.model.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface Authentication {
    /**
     * Returns the supported authentication methods by ZAP.
     * @return list of supported authentication methods.
     * @throws ProxyException
     */
    List<String> getSupportedAuthenticationMethods() throws ProxyException;

    /**
     * Returns logged in indicator pattern for the given context.
     * @param contextId Id of the context.
     * @return Logged in indicator for the given context.
     * @throws ProxyException
     */
    String getLoggedInIndicator(String contextId) throws ProxyException;

    /**
     * Returns logged out indicator pattern for the given context.
     * @param contextId Id of the context.
     * @return Logged out indicator for the given context.
     * @throws ProxyException
     */
    String getLoggedOutIndicator(String contextId) throws ProxyException;

    /**
     * Sets the logged in indicator to a given context.
     * @param contextId Id of a context.
     * @param loggedInIndicatorRegex Regex pattern for logged in indicator.
     * @throws ProxyException
     */
    void setLoggedInIndicator(String contextId, String loggedInIndicatorRegex) throws ProxyException;

    /**
     * Sets the logged out indicator to a given context.
     * @param contextId Id of a context.
     * @param loggedOutIndicatorRegex Regex pattern for logged out indicator.
     * @throws ProxyException
     */
    void setLoggedOutIndicator(String contextId, String loggedOutIndicatorRegex) throws ProxyException;

    /**
     * Returns authentication method for a given context.
     * @param contextId Id of a context.
     * @return Authentication method details for the given context id.
     * @throws ProxyException
     */
    Map<String, String> getAuthenticationMethodInfo(String contextId) throws ProxyException;

    /**
     * Returns the list of authentication config parameters.
     * Each config parameter is a map with keys "name" and "mandatory", holding the values name of the configuration parameter and whether it is mandatory/optional respectively.
     * @param authMethod Valid authentication method name.
     * @return List of configuration parameters for the given authentication method name.
     * @throws ProxyException
     */
    List<Map<String, String>> getAuthMethodConfigParameters(String authMethod) throws ProxyException;

    /**
     * Sets the authentication method for a given context with given configuration parameters.
     * @param contextId Id of a context.
     * @param authMethodName Valid authentication method name.
     * @param authMethodConfigParams Authentication method configuration parameters such as loginUrl, loginRequestData formBasedAuthentication method, and hostName, port, realm for httpBasedAuthentication method.
     * @throws ProxyException
     */
    void setAuthenticationMethod(String contextId, String authMethodName, String authMethodConfigParams) throws ProxyException;

    /**
     * Sets the formBasedAuthentication to given context id with the loginUrl and loginRequestData.
     * Example loginRequestData: "username={%username%}&password={%password%}"
     * @param contextId Id of the context.
     * @param loginUrl Login URL.
     * @param loginRequestData Login request data with form field names for username and password.
     * @throws ProxyException
     * @throws UnsupportedEncodingException
     */
    void setFormBasedAuthentication(String contextId, String loginUrl, String loginRequestData) throws ProxyException, UnsupportedEncodingException;

    /**
     * Sets the HTTP/NTLM authentication to given context id with hostname, realm and port.
     * @param contextId Id of the context.
     * @param hostname Hostname.
     * @param realm Realm.
     * @param portNumber Port number.
     * @throws ProxyException
     */
    void setHttpAuthentication(String contextId, String hostname, String realm, String portNumber) throws ProxyException, UnsupportedEncodingException;

    /**
     * Sets the HTTP/NTLM authentication to given context id with hostname, realm.
     * @param contextId Id of the context.
     * @param hostname Hostname.
     * @param realm Realm.
     * @throws ProxyException
     */
    void setHttpAuthentication(String contextId, String hostname, String realm) throws ProxyException, UnsupportedEncodingException;

    /**
     * Sets the manual authentication to the given context id.
     * @param contextId Id of the context.
     * @throws ProxyException
     */
    void setManualAuthentication(String contextId) throws ProxyException;

    /**
     * Sets the script based authentication to the given context id with the script name and config parameters.
     * @param contextId Id of the context.
     * @param scriptName Name of the script.
     * @param scriptConfigParams Script config parameters.
     * @throws ProxyException
     */
    void setScriptBasedAuthentication(String contextId, String scriptName, String scriptConfigParams) throws ProxyException, UnsupportedEncodingException;

    /**
     * Returns list of {@link User}s for a given context.
     * @param contextId Id of the context.
     * @return List of {@link User}s
     * @throws ProxyException
     * @throws IOException
     */
    List<User> getUsersList(String contextId) throws ProxyException, IOException;

    /**
     * Returns the {@link User} info for a given context id and user id.
     * @param contextId Id of a context.
     * @param userId Id of a user.
     * @return {@link User} info.
     * @throws ProxyException
     * @throws IOException
     */
    User getUserById(String contextId, String userId) throws ProxyException, IOException;

    /**
     * Returns list of config parameters of authentication credentials for a given context id.
     * Each item in the list is a map with keys "name" and "mandatory".
     * @param contextId Id of a context.
     * @return List of authentication credentials configuration parameters.
     * @throws ProxyException
     */
    List<Map<String, String>> getAuthenticationCredentialsConfigParams(String contextId) throws ProxyException;

    /**
     * Returns the authentication credentials as a map with key value pairs for a given context id and user id.
     * @param contextId Id of a context.
     * @param userId Id of a user.
     * @return Authentication credentials.
     * @throws ProxyException
     */
    Map<String, String> getAuthenticationCredentials(String contextId, String userId) throws ProxyException;

    /**
     * Creates a new {@link User} for a given context and returns the user id.
     * @param contextId Id of a context.
     * @param name Name of the user.
     * @return User id.
     * @throws ProxyException
     */
    String newUser(String contextId, String name) throws ProxyException;

    /**
     * Removes a {@link User} using the given context id and user id.
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId Id of a {@link User}
     * @throws ProxyException
     */
    void removeUser(String contextId, String userId) throws ProxyException;

    /**
     * Sets the authCredentialsConfigParams to the given context and user.
     * Bu default, authCredentialsConfigParams uses key value separator "=" and key value pair separator "&".
     * Make sure that values provided for authCredentialsConfigParams are URL encoded using "UTF-8".
     * @param contextId Id of the context.
     * @param userId Id of the user.
     * @param authCredentialsConfigParams Authentication credentials config parameters.
     * @throws ProxyException
     */
    void setAuthenticationCredentials(String contextId, String userId, String authCredentialsConfigParams) throws ProxyException;

    /**
     * Enables a {@link User} for a given {@link net.continuumsecurity.proxy.model.Context} id and user id.
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId Id of a {@link User}
     * @param enabled Boolean value to enable/disable the user.
     * @throws ProxyException
     */
    void setUserEnabled(String contextId, String userId, boolean enabled) throws ProxyException;

    /**
     * Sets a name to the user for the given context id and user id.
     * @param contextId Id of a {@link net.continuumsecurity.proxy.model.Context}
     * @param userId Id of a {@link User}
     * @param name User name.
     * @throws ProxyException
     */
    void setUserName(String contextId, String userId, String name) throws ProxyException;

    /**
     * Returns the forced user id for a given context.
     * @param contextId Id of a context.
     * @return Id of a forced {@link User}
     * @throws ProxyException
     */
    String getForcedUserId(String contextId) throws ProxyException;

    /**
     * Returns true if forced user mode is enabled. Otherwise returns false.
     * @return true if forced user mode is enabled.
     * @throws ProxyException
     */
    boolean isForcedUserModeEnabled() throws ProxyException;

    /**
     * Enables/disables the forced user mode.
     * @param forcedUserModeEnabled flag to enable/disable forced user mode.
     * @throws ProxyException
     */
    void setForcedUserModeEnabled(boolean forcedUserModeEnabled) throws ProxyException;

    /**
     * Sets a {@link User} id as forced user for the given {@link net.continuumsecurity.proxy.model.Context}
     * @param contextId Id of a context.
     * @param userId Id of a user.
     * @throws ProxyException
     */
    void setForcedUser(String contextId, String userId) throws ProxyException;

    /**
     * Returns list of supported session management methods.
     * @return List of supported session management methods.
     * @throws ProxyException
     */
    List<String> getSupportedSessionManagementMethods() throws ProxyException;

    /**
     * Returns session management method selected for the given context.
     * @param contextId Id of a context.
     * @return Session management method for a given context.
     * @throws ProxyException
     */
    String getSessionManagementMethod(String contextId) throws ProxyException;

    /**
     * Sets the given session management method and config params for a given context.
     * @param contextId Id of a context.
     * @param sessionManagementMethodName Session management method name.
     * @param methodConfigParams Session management method config parameters.
     * @throws ProxyException
     */
    void setSessionManagementMethod(String contextId, String sessionManagementMethodName, String methodConfigParams) throws ProxyException;
}
