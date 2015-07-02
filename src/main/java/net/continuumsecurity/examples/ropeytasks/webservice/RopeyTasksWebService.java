package net.continuumsecurity.examples.ropeytasks.webservice;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.clients.GenericClient;
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

    }

    @Override
    public void enableDefaultClient() {

    }

    @Override
    public GenericClient getClient() {
        return client;
    }

    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        client.login(creds.getUsername(),creds.getPassword());
    }

    @Override
    public void openLoginPage() {

        Response response = client.get(Config.getInstance().getBaseUrl());
        System.out.println(response.getStatus());
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void logout() {

    }
}
