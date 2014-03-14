package net.continuumsecurity.scanner;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PortScanner {
    String host;
    int startPort, endPort, threads,msTimeout;

    public PortScanner(String host, int startPort, int endPort, int threads, int msTimeout) {
        this.host = host;
        this.startPort = startPort;
        this.endPort = endPort;
        this.msTimeout = msTimeout;
        this.threads = threads;
    }

    public List<PortResult> scan() throws ExecutionException, InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(threads);
        final String ip = host;
        final int timeout = msTimeout;
        final List<Future<PortResult>> futures = new ArrayList<Future<PortResult>>();
        for (int port = startPort; port <= endPort; port++) {
            futures.add(scanPort(es, ip, port, timeout));
        }
        es.shutdown();
        List<PortResult> results = new ArrayList<PortResult>();
        for (Future<PortResult> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    public static Future<PortResult> scanPort(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<PortResult>() {

            @Override
            public PortResult call() {
                PortResult result = new PortResult(port);
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    result.setState(PortResult.PortState.OPEN);
                } catch (SocketTimeoutException ste) {
                    result.setState(PortResult.PortState.TIMEDOUT);
                } catch (Exception ex) {
                    result.setState(PortResult.PortState.CLOSED);
                }
                return result;
            }
        });
    }
}
