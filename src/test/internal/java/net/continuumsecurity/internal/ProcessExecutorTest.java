package net.continuumsecurity.internal;

import net.continuumsecurity.ProcessExecutor;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by stephen on 18/01/15.
 */
public class ProcessExecutorTest {
    ProcessExecutor executor;

    @Test
    public void testThatExecutionErrorThrowsException() {
        executor = new ProcessExecutor("cat /etc/passwd");
        try {
            executor.start();
            fail("Should throw exception");
        } catch (IOException e) {

        }
    }


}
