package net.continuumsecurity.internal.scanner;

import net.continuumsecurity.scanner.PortResult;
import net.continuumsecurity.scanner.PortScanner;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PortScannerTest {
    PortScanner scanner;

    @Test
    public void testPortOpen() throws ExecutionException, InterruptedException {
        scanner = new PortScanner("www.google.com",80,80,1,500);
        List<PortResult> results = scanner.scan();
        assert results.size() == 1;
        assert results.get(0).getPort() == 80;
        assert results.get(0).getState() == PortResult.PortState.OPEN;
    }


    @Test
    public void testPortClosed() throws ExecutionException, InterruptedException {
        scanner = new PortScanner("127.0.0.1",65531,65531,1,500);
        List<PortResult> results = scanner.scan();
        assert results.size() == 1;
        assert results.get(0).getPort() == 65531;
        assert results.get(0).getState() == PortResult.PortState.CLOSED;
    }

    @Test
    public void testPortTimedOut() throws ExecutionException, InterruptedException {
        scanner = new PortScanner("www.google.com",83,83,1,500);
        List<PortResult> results = scanner.scan();
        assert results.size() == 1;
        assert results.get(0).getPort() == 83;
        assert results.get(0).getState() == PortResult.PortState.TIMEDOUT;
    }

}
