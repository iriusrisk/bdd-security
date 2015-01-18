package net.continuumsecurity.internal.jbehave;

import net.continuumsecurity.web.steps.AppScanningSteps;
import org.junit.Before;
import org.junit.Test;
import org.zaproxy.clientapi.core.Alert;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

public class TestAutomatedScannerSteps {
    Alert first;
    Alert second;
    Alert third;
    AppScanningSteps steps;

    @Before
    public void setup() {
        first = new Alert("alert1", "url1", Alert.Risk.High, Alert.Reliability.Warning, "param1", "other1");
        second = new Alert("alert2", "url2", Alert.Risk.High, Alert.Reliability.Warning, "param2", "other2");
        third = new Alert("alert3", "url3", Alert.Risk.High, Alert.Reliability.Warning, "param3", "other3");

        steps = new AppScanningSteps();
    }

    @Test
    public void testAlertsMatchByValue() {
        Alert copy = new Alert("alert1", "url1", Alert.Risk.High, Alert.Reliability.Warning, "param1", "other1");
        assertThat(steps.alertsMatchByValue(first,copy), is(true));
        assertThat(steps.alertsMatchByValue(first, second), is(false));
    }

    @Test
    public void testContainsAlertByValue() {
        List<Alert> list1 = new ArrayList<>();
        list1.add(first);

        assertThat(steps.containsAlertByValue(list1, first), is(true));
        assertThat(steps.containsAlertByValue(list1,second),is(false));
    }



}
