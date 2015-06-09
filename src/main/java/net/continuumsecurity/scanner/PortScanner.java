package net.continuumsecurity.scanner;

import net.continuumsecurity.Port;

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

    public List<Port> scan() throws ExecutionException, InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(threads);
        final String ip = host;
        final int timeout = msTimeout;
        final List<Future<Port>> futures = new ArrayList<Future<Port>>();
        for (int port = startPort; port <= endPort; port++) {
            futures.add(scanPort(es, ip, port, timeout));
        }
        es.shutdown();
        List<Port> results = new ArrayList<Port>();
        for (Future<Port> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    public static Future<Port> scanPort(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<Port>() {

            @Override
            public Port call() {
                Port result = new Port(port);
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    result.setState(Port.State.OPEN);
                } catch (SocketTimeoutException ste) {
                    result.setState(Port.State.TIMEDOUT);
                } catch (Exception ex) {
                    result.setState(Port.State.CLOSED);
                }
                return result;
            }
        });
    }
}
