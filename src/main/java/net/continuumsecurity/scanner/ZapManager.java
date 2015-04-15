package net.continuumsecurity.scanner;

import org.zaproxy.zap.ZAP;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by stephen on 12/04/15.
 */
public class ZapManager {

    private static ZapManager instance = null;
    private int port;

    private ZapManager() {
    }

    public static ZapManager getInstance() {
        if (instance == null) instance = new ZapManager();
        return instance;
    }

    public int start() throws Exception {
        port = findOpenPortOnAllLocalInterfaces();
        port = 65419;
        String[] args = new String[]{"-daemon", "-host", "127.0.0.1", "-port", Integer.toString(port), "-installdir", "owasp_zap"};
        System.out.println("Starting ZAP with args: "+args.toString());
        ZAP.main(args);
        return port;
    }

    private Integer findOpenPortOnAllLocalInterfaces() throws IOException {
        try (
                ServerSocket socket = new ServerSocket(0);
        ) {
            port = socket.getLocalPort();
            socket.close();
            return port;
        }
    }

}
