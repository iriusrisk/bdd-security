package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.clients.SessionClient;
import net.continuumsecurity.web.Application;

import javax.ws.rs.core.Response;

/**
 * Created by stephen on 30/06/15.
 */
public class RopeyTasksWebService extends Application implements ILogin, ILogout {
    RopeyClient client;

    public RopeyTasksWebService() {
        client = new RopeyClient();
    }

    @Override
    public void enableHttpLoggingClient() {
        //client is an httpLoggingClient by default and can't be disabled
    }

    @Override
    public void enableDefaultClient() {
        //client is an httpLoggingClient by default and can't be disabled
    }

    @Override
    public SessionClient getClient() {
        return client;
    }

    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        client.login(creds.getUsername(),creds.getPassword());
    }

    @Override
    public void openLoginPage() {
        //Do nothing, N/A to web service
    }

    @Override
    public boolean isLoggedIn() {
        Response response = client.get("task/list");
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("Welcome")) return true;
        return false;
    }

    @Override
    public void logout() {
        client.get("user/logout");
    }

    @Restricted(users = {"bob", "admin"},
            sensitiveData = "Robert")
    public void viewProfileForBob() {
        client.get("user/edit/1");
    }

    public void navigate() {
        login(Config.getInstance().getUsers().getDefaultCredentials());
        client.get("task/list");
        if (!client.getLastResponse().readEntity(String.class).contains("Welcome")) throw new UnexpectedContentException("Expected text: 'Welcome' was not found.");
        client.getWebClientWithSessionCookie().path("/task/search").query("q","test").query("search","Search").get();
    }
}
