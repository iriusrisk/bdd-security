package net.continuumsecurity.scanner;

import net.continuumsecurity.Config;
import org.zaproxy.clientapi.core.ClientApi;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephen on 12/04/15.
 */
public class ZapManager {
    private final static Logger log = Logger.getLogger(ZapManager.class.getName());
    private static ZapManager instance = null;
    private int port;
    String HOST = "127.0.0.1";
    int CONNECTION_TIMEOUT = 15000; //milliseconds
    public static final String API_KEY = "zapapisecret";
    Process process;

    private ZapManager() {
    }

    public static synchronized ZapManager getInstance() {
        if (instance == null) instance = new ZapManager();
        return instance;
    }

    public int startZAP(String zapPath) throws Exception {
        if (process == null) {
            File zapProgramFile = new File(zapPath);
            port = findOpenPortOnAllLocalInterfaces();
            List<String> params = new ArrayList<>();
            params.add(zapProgramFile.getAbsolutePath());
            params.add("-daemon");
            params.add( "-host");  params.add( HOST);
            params.add("-port"); params.add(String.valueOf(port));
            params.add("-dir"); params.add("tmp");
            params.add("-config"); params.add("scanner.threadPerHost=20");
            params.add("-config"); params.add("spider.thread=10");
            params.add("-config"); params.add("api.key="+API_KEY);
            Config.getInstance().setProxyApi(API_KEY);
            String upstreamProxyHost = Config.getInstance().getUpstreamProxyHost();
            if (!upstreamProxyHost.isEmpty()) {
                int upstreamProxyPort = Config.getInstance().getUpstreamProxyPort();
                log.info("Setting upstream proxy for ZAP to: "+upstreamProxyHost+":"+upstreamProxyPort);
                params.add("-config"); params.add("connection.proxyChain.hostName="+upstreamProxyHost);
                params.add("-config"); params.add("connection.proxyChain.port="+upstreamProxyPort);
                params.add("-config"); params.add("connection.proxyChain.enabled=true");
            }
            log.info("Start ZAProxy [" + zapProgramFile.getAbsolutePath() + "] on port: " + port);
            ProcessBuilder pb = new ProcessBuilder().inheritIO();
            pb.directory(zapProgramFile.getParentFile());
            process = pb.command(params.toArray(new String[params.size()])).start();
            waitForSuccessfulConnectionToZap();
        } else {
            log.info("ZAP already started.");
        }
        return port;
    }

    public void stopZap() {
        if (process == null) return; //ZAP not running
        try {
            log.info("Stopping ZAP");
            ClientApi client = new ClientApi(HOST,port,API_KEY);
            client.core.shutdown();
            Thread.sleep(2000);
            process.destroy();
        } catch (final Exception e) {
            log.warning("Error shutting down ZAP.");
            log.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void waitForSuccessfulConnectionToZap() {
        int timeoutInMs = CONNECTION_TIMEOUT;
        int connectionTimeoutInMs = timeoutInMs;
        int pollingIntervalInMs = 1000;
        boolean connectionSuccessful = false;
        long startTime = System.currentTimeMillis();
        Socket socket = null;
        do {
            try {
                log.info("Attempting to connect to ZAP API on: "+HOST+" port: "+port);
                socket = new Socket();
                socket.connect(new InetSocketAddress(HOST, port), connectionTimeoutInMs);
                connectionSuccessful = true;
                log.info("Connected to ZAP");
            } catch (SocketTimeoutException ignore) {
                throw new RuntimeException("Unable to connect to ZAP's proxy after " + timeoutInMs + " milliseconds.");
            } catch (IOException ignore) {
                // and keep trying but wait some time first...
                try {
                    Thread.sleep(pollingIntervalInMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException("The task was interrupted while sleeping between connection polling.", e);
                }

                long ellapsedTime = System.currentTimeMillis() - startTime;
                if (ellapsedTime >= timeoutInMs) {
                    throw new RuntimeException("Unable to connect to ZAP's proxy after " + timeoutInMs + " milliseconds.");
                }
                connectionTimeoutInMs = (int) (timeoutInMs - ellapsedTime);
            } finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while (!connectionSuccessful);
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
