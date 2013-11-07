package net.continuumsecurity.internal;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class TestEnvironment {
    Server server;

    public TestEnvironment() {
        server = new Server(9110);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("resources/ropeytasks-0.1.war");
        server.setHandler(webapp);
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
