package net.continuumsecurity.scanner;

import org.zaproxy.clientapi.core.ClientApi;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
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
    String API_KEY = "";
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
            String[] cmd = {zapProgramFile.getAbsolutePath(), "-daemon",
                    "-host", HOST,
                    "-port", String.valueOf(port)};
            log.info("Start ZAProxy [" + zapProgramFile.getAbsolutePath() + "] on port: " + port);
            ProcessBuilder pb = new ProcessBuilder().inheritIO();
            pb.directory(zapProgramFile.getParentFile());

            process = pb.command(cmd).start();
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
            ClientApi client = new ClientApi(HOST,port);
            client.core.shutdown(API_KEY);
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
                socket = new Socket();
                socket.connect(new InetSocketAddress(HOST, port), connectionTimeoutInMs);
                connectionSuccessful = true;
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
