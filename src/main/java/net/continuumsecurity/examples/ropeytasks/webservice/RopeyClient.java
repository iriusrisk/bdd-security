package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.Config;
import net.continuumsecurity.clients.GenericClient;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * Created by stephen on 30/06/15.
 */
public class RopeyClient implements GenericClient{
    WebClient webClient;
    String sessionID;

    public RopeyClient() {
        webClient = WebClient.create(Config.getInstance().getBaseUrl());
        ClientConfiguration config = webClient.getConfig(webClient);
        config.getHttpConduit().getClient().setProxyServer(Config.getInstance().getProxyHost());
        config.getHttpConduit().getClient().setProxyServerPort(Config.getInstance().getProxyPort());
    }

    public void login(String username, String password) {
        MultivaluedHashMap<String,String> postBody = new MultivaluedHashMap<>();
        postBody.put("username",username);
        webClient.path("user/index").accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(postBody);
    }


    public Response get(String path) {
        return webClient.path(path).get();
    }

    @Override
    public void clearAuthenticationTokens() {

    }
}
