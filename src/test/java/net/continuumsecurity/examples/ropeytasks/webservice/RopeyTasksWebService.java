package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.*;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.INavigable;
import net.continuumsecurity.clients.AuthTokenManager;
import net.continuumsecurity.web.Application;

import javax.ws.rs.core.Response;

/**
 *
 * Example of how to test a web service. In this case, we're testing the same 'ropeytasks' web application,
 * but instead of using a browser, we're using a bespoke HTTP client built with Apache CXF and we're
 * pretending that the web app is an HTTP API.
 *
 */
public class RopeyTasksWebService extends Application implements ILogin, ILogout,INavigable {
    RopeyClient client;

    public RopeyTasksWebService() {
        client = new RopeyClient();
    }

    @Override
    public void enableHttpLoggingClient() {
        //client uses ZAP to log HTTP traffic by default and can't be disabled
    }

    @Override
    public void enableDefaultClient() {
        //client uses ZAP to log HTTP traffic by default and can't be disabled
    }

    @Override
    public AuthTokenManager getAuthTokenManager() {
        return client;
    }

    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        client.login(creds.getUsername(),creds.getPassword());
    }

    @Override
    public void openLoginPage() {
        // Make un-authenticated request to any webService endpoint that generates a session token
        client.get("/");
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

    public void viewProfileForBob() {
        client.get("user/edit/1");
    }

    /*
    This method is called before spidering and scanning the service. It should include a workflow that excercises all
    of the key service methods
     */
    public void navigate() {
        login(Config.getInstance().getUsers().getDefaultCredentials());
        client.get("task/list");
        if (!client.getLastResponse().readEntity(String.class).contains("Welcome")) throw new UnexpectedContentException("Expected text: 'Welcome' was not found.");
        client.getWebClientWithSessionCookie().path("/task/search").query("q","test").query("search","Search").get();
    }
}
