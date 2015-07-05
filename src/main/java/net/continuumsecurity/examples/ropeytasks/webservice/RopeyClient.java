package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.Config;
import net.continuumsecurity.clients.AuthTokenManager;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.*;
import java.util.*;

/**
 * Created by stephen on 30/06/15.
 */
public class RopeyClient implements AuthTokenManager {
    WebClient webClient;
    String sessionID;
    static final String SESSION_ID_NAME="JSESSIONID";
    Response lastResponse;

    public RopeyClient() {
        webClient = WebClient.create(Config.getInstance().getBaseUrl());
        ClientConfiguration config = webClient.getConfig(webClient);
        config.getHttpConduit().getClient().setProxyServer(Config.getInstance().getProxyHost());
        config.getHttpConduit().getClient().setProxyServerPort(Config.getInstance().getProxyPort());
    }

    public void login(String username, String password) {
        Form postBody = new Form();
        postBody.param("username",username).param("password",password).param("_action_login","Login");
        lastResponse = post("user/index", postBody);
    }

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

    @Override
    public Map<String,String> getAuthTokens() {
        Map<String,String> tokens = new HashMap<>();
        tokens.put(SESSION_ID_NAME,sessionID);
        return tokens;
    }

    @Override
    public void setAuthTokens(Map<String, String> tokens) {
        sessionID = tokens.get(SESSION_ID_NAME);
    }

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
}
