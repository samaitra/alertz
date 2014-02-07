package com.flipkart.alert.resource;

import com.flipkart.alert.dispatch.HttpAlertQueue;
import com.flipkart.alert.domain.*;
import com.flipkart.alert.integration.BaseIntegrationTest;
import com.flipkart.alert.testHelper.PolyMorphicRuleMixin;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.*;

/**
 * nitinka
 * Metric Source unit tests
 */
public class TestHttpAlertQueueResource  extends BaseIntegrationTest {
    private Alert alert;
    @BeforeClass
    public void setupData() throws IOException {
        mapper.getDeserializationConfig().addMixInAnnotations(Rule.class, PolyMorphicRuleMixin.class);
        alert = mapper.readValue(jsonFixture("fixtures/alert.json"), Alert.class);
    }

    @AfterMethod
    public void methodTeardown() throws Exception {
        HttpAlertQueue.removeAllQueues();
    }

    @Test(groups = {"regression","resource"})
    public void testGetAllAlertsNoQueues() {
        assertThat("Alert Meta With No Queues",
                httpAlertQueueClient.getAllAlerts(),
                is(alertsMetaWithNoQueues()));
    }

    @Test(groups = {"smoke","regression","resource"})
    public void testGetAllAlertsSingleQueueNoAlert() {
        HttpAlertQueue.createQueue("defaultQueue");
        assertThat("Alert Meta With Single Queue No Alert",
                httpAlertQueueClient.getAllAlerts(),
                is(alertsMetaWithSingleQueueNoAlerts("defaultQueue")));
    }

    @Test(groups = {"smoke","regression","resource"})
    public void testGetAllAlertsSingleQueue1Alert() throws InterruptedException {
        HttpAlertQueue.add(new Alert(), "testQueue");
        assertThat("Alert Meta With sinle Queue and Single alert",
                httpAlertQueueClient.getAllAlerts(),
                is(alertsMetaWithSingleQueue("testQueue", 1)));
    }

    @Test(groups = {"smoke","regression","resource"})
    public void testGetAllAlertsMultipleQueuesmultipleAlerts() throws InterruptedException {
        HttpAlertQueue.add(new Alert(),"testQueue1");
        HttpAlertQueue.add(new Alert(),"testQueue1");
        HttpAlertQueue.add(new Alert(),"testQueue2");
        Map expectedAlertsMeta = new HashMap<String, Integer>();
        expectedAlertsMeta.putAll(alertsMetaWithSingleQueue("testQueue1", 2));
        expectedAlertsMeta.putAll(alertsMetaWithSingleQueue("testQueue2", 1));

        assertThat("Alert Meta With Multiple Queues",
                httpAlertQueueClient.getAllAlerts(),
                is(expectedAlertsMeta));
    }

    @Test(groups = {"regression","resource"})
    public void testAlertQueueMetaNonExistentQueue() {
        assertThat("Queue Alert Meta With Non Existent Queue",
                httpAlertQueueClient.getAlertQueueMeta("non-existent"),
                is(0));
    }

    @Test(groups = {"smoke","regression","resource"})
    public void testAlertQueueMetaZeroAlerts() {
        HttpAlertQueue.createQueue("testQueue");
        assertThat("Queue Alert Meta With Zero Queue",
                httpAlertQueueClient.getAlertQueueMeta("testQueue"),
                is(0));
    }

    @Test(groups = {"smoke","regression","resource"})
    public void testAlertQueueMetaHavingAlerts() throws InterruptedException {
        HttpAlertQueue.add(new Alert(),"testQueue");
        HttpAlertQueue.add(new Alert(), "testQueue");
        assertThat("Queue Alert Meta Having alerts",
                httpAlertQueueClient.getAlertQueueMeta("testQueue"),
                is(2));
    }

    @Test(groups = {"regression","resource", "negative"})
    public void testGetAlertFromNonExistentQueue() throws InterruptedException {
        List alerts = new ArrayList<Alert>();
        assertThat("Get Alert from Not Existent Queue",
                httpAlertQueueClient.getAlerts("nonExistent"),
                is(alerts));

    }

    @Test(groups = {"regression","resource", "negative"})
    public void testGetAlertFromEmptyQueue() throws InterruptedException {
        HttpAlertQueue.createQueue("testQueue");
        List alerts = new ArrayList<Alert>();
        assertThat("Get Alert from empty Queue",
                httpAlertQueueClient.getAlerts("testQueue"),
                is(alerts));

    }

//    @Test(groups = {"regression","resource", "negative"})
//    public void testGetAlertNagiosFormat() throws InterruptedException {
//        HttpAlertQueue.add(alert.setAlertDate(new Date()).resolveCheckDescriptionWithMetricValues(), "testQueue");
//        assertThat("Nagios Formated Alert",
//                httpAlertQueueClient.getNagiosFormattedAlert("testQueue"),
//                is(nagiosFormatedAlert(alert)));
//
//    }

    private Map nagiosFormatedAlert(Alert alert) {
        Map<String,String> nagiosAlertInfoMap = new HashMap<String, String>();
        List<Metric> metrics = alert.getMetrics();
       String metricStr = "";

        for(Metric metric: metrics) {
            metricStr += metric.getKey() + "=" + metric.getValue() + ",";
        }
        if(metricStr.length()>0)
            metricStr = metricStr.substring(0, metricStr.length()-1);

        List <RuleCheck> breachedChecks = alert.getBreachedChecks();
        String description= "";
        List<String> alertLevels = new ArrayList<String>();
        for (RuleCheck breachedCheck : breachedChecks) {
            description  = description + breachedCheck.getDescription() + ' ';
            alertLevels.add(breachedCheck.getAlertLevel());
        }

        if(description == null || description.trim().equals("")) {
            for (RuleCheck breachedCheck : breachedChecks) {
                description  = description + breachedCheck.getBooleanExpression() + ' ';
            }
        }
        nagiosAlertInfoMap.put("message",
                String.format(alert.getAlertDate().toString()
                        + ": Metrics (%s) breached check with description '%s'",
                        metricStr,
                        description));

        nagiosAlertInfoMap.put("alertLevel", Collections.min(alertLevels));

        return nagiosAlertInfoMap;

    }

    private Map alertsMetaWithSingleQueue(String queueName, int alertsCount) {
        Map<String,Integer> alertsMetaMap = new HashMap<String,Integer>();
        alertsMetaMap.put(queueName, alertsCount);
        return alertsMetaMap;
    }

    private Map alertsMetaWithSingleQueueNoAlerts(String queueName) {
        Map<String,Integer> alertsMetaMap = new HashMap<String,Integer>();
        alertsMetaMap.put(queueName, 0);
        return alertsMetaMap;
    }

    private Map alertsMetaWithNoQueues() {
        return new HashMap<String,Integer>();
    }
}


