package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.Config;
import net.continuumsecurity.clients.AuthTokenManager;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.*;
import java.util.*;

/**
 * Created by stephen on 30/06/15.
 *
 */
public class RopeyClient implements AuthTokenManager {
    WebClient webClient;
    String sessionID;
    static final String SESSION_ID_NAME="JSESSIONID";
    Response lastResponse;

    /*
    Create the HTTP client and configure it to use ZAP as a proxy.  Since we don't have the overhead of a browser, there's no need to
    configure two different clients, one that uses ZAP and one that doesn't.  Instead we'll just configure 1 client that always uses
    ZAP.
     */
    public RopeyClient() {
        webClient = WebClient.create(Config.getInstance().getBaseUrl());
        ClientConfiguration config = webClient.getConfig(webClient);
        config.getHttpConduit().getClient().setProxyServer(Config.getInstance().getProxyHost());
        config.getHttpConduit().getClient().setProxyServerPort(Config.getInstance().getProxyPort());
    }

    /*
    Return a name-value map of the current authentication tokens used by the client.  In this example, the API uses a JSESSIONID
    in a cookie as the authentication token, and we're tracking it manually using the sessionID member variable.
     */
    @Override
    public Map<String,String> getAuthTokens() {
        Map<String,String> tokens = new HashMap<>();
        tokens.put(SESSION_ID_NAME,sessionID);
        return tokens;
    }

    /*
    Set all the authentication tokens required by the client using a name-value map.  In this example, the API uses a JSESSIONID
    in a cookie as the authentication token, so we expect the name to be JSESSIONID.
     */
    @Override
    public void setAuthTokens(Map<String, String> tokens) {
        sessionID = tokens.get(SESSION_ID_NAME);
    }

    /*
    Delete all the authentication tokens, in this example this just means the JSESSIONID value.
     */
    @Override
    public void deleteAuthTokens() {
        sessionID = null;
    }

    public WebClient getWebClientWithSessionCookie() {
        webClient.reset();
        if (sessionID != null) {
            Cookie cookie = new Cookie(SESSION_ID_NAME,sessionID);
            return webClient.cookie(cookie);
        }
        return webClient;
    }

    /*
    Convenience method that will be called from the ILogin interface on the Application getInstance
     */
    public void login(String username, String password) {
        Form postBody = new Form();
        postBody.param("username",username).param("password",password).param("_action_login","Login");
        lastResponse = post("user/index", postBody);
    }

    /*
    Convenience method, and since we're pretending to be a browser, add the session ID cookie from any previous responses
    to the request.
     */
    public Response get(String path) {
        lastResponse = getWebClientWithSessionCookie().path(path).get();
        setCookieFromResponse(lastResponse);  //mimic browser behaviour
        return lastResponse;
    }

    private void setCookieFromResponse(Response lastResponse) {
        NewCookie newCookie = lastResponse.getCookies().get(SESSION_ID_NAME);
        if (newCookie != null) sessionID = newCookie.getValue();
    }

    public Response post(String path, Form form) {
        lastResponse = getWebClientWithSessionCookie().path(path).accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(form);
        setCookieFromResponse(lastResponse); //mimic browser behaviour
        return lastResponse;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
}
