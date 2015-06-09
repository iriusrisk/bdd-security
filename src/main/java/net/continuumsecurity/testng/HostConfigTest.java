package net.continuumsecurity.testng;

import net.continuumsecurity.steps.InfrastructureSteps;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Created by stephen on 12/02/15.
 */
public class HostConfigTest {
    InfrastructureSteps steps = new InfrastructureSteps();

    @Test
    public void test_open_ports() throws MalformedURLException, ExecutionException, InterruptedException {
        steps.setTargetHost("localhost");
        steps.scanPorts(1, 1024, 10, 300);
        steps.selectPorts("open");
        assertThat(steps.getSelectedPorts(), contains(80,443));
    }
}
