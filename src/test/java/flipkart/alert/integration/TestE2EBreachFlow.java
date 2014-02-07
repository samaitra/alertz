package flipkart.alert.integration;

import flipkart.alert.dispatch.HttpAlertQueue;
import flipkart.alert.domain.*;
import flipkart.alert.schedule.Scheduler;
import flipkart.alert.storage.GraphiteHttpClient;
import flipkart.alert.storage.MetricSourceClientFactory;
import flipkart.alert.testHelper.PolyMorphicRuleMixin;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.SchedulerException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
* Created by IntelliJ IDEA.
* User: nitinka
* Date: 11/12/12
* Time: 4:10 PM
* To change this template use File | Settings | File Templates.
*/
public class TestE2EBreachFlow extends BaseIntegrationTest {

    private ObjectMapper mapper = new ObjectMapper();
    private List<Metric> metrics = new ArrayList<Metric>();

    @BeforeClass
    public void beforeClass() throws Exception {

        MetricSource metricSource = mapper.readValue(jsonFixture("fixtures/scm.graphite.json"), MetricSource.class);
        mapper.getDeserializationConfig().addMixInAnnotations(Rule.class, PolyMorphicRuleMixin.class);

        MetricSourceClientFactory spyFactory = spy(metricSourceClientFactory);
        spyFactory.addMetricSource(metricSource);

        GraphiteHttpClient graphiteHttpClient = new GraphiteHttpClient(metricSource.paramsToMap());
        GraphiteHttpClient spyGraphiteHttpClient = spy(graphiteHttpClient);

        spyFactory.setClient("scm.graphite", spyGraphiteHttpClient);
        metrics = currentDateAdjustedData("series1","fixtures/graphiteData.json");
        doReturn(spyGraphiteHttpClient).when(spyFactory).getClient("scm.graphite");
        doReturn(metrics).when(spyGraphiteHttpClient).execute();
    }

    @BeforeClass
    public void beforeMethod() throws SchedulerException, IOException {
        for(String tableName : new String[]{"RULES"})
            ScheduledRule.deleteAll(tableName);
    }

    @AfterClass
    public void afterClass() throws SchedulerException {
        Scheduler.getScheduler().removeAllJobs();
        HttpAlertQueue.removeAllQueues();
        for(String tableName : new String[]{"RULES"})
            ScheduledRule.deleteAll(tableName);
    }

    @Test(groups = {"smoke","regression","e2e"})
    public void e2e() throws IOException, InterruptedException {
        // Create Rule
        ScheduledRule rule = scheduledRuleClient.createRule("fixtures/rule.e2e.breach.json");
        Set<CheckStat> checkStats = new HashSet<CheckStat>();

        RuleStat stat = new RuleStat.RuleStatBuilder()
                .withBreached(true)
                .build();

        for(RuleCheck check: rule.getChecks()) {
            CheckStat checkStat = new CheckStat.CheckStatBuilder()
                    .withBreached(true)
                    .withExpression(check.getBooleanExpression())
                    .withDescription(resolveDescription(check.getDescription()))
                    .build();
            checkStats.add(checkStat);
        }
        stat.setCheckStats(checkStats);

        Thread.sleep(5000);

        List<RuleStat> actualStats = scheduledRuleClient.getAllRuleStats();
        assertThat("Number of Statistics. Single Rule executed once",
                actualStats.size(), is(1));


        System.out.println("Comparing rule stats" + actualStats.get(0).equals(stat));
        assertThat("Compare Rule Stat. Single rule executed 1 time",
                actualStats.get(0),
                is(stat));
    }

    private List<Metric> currentDateAdjustedData(String seriesName,String dataFile) throws IOException {
        List<Metric> metrics = new ArrayList<Metric>();
        List<LinkedHashMap<String,Object>> data = mapper.readValue(jsonFixture(dataFile), List.class);
        LinkedHashMap<String,Object> series = data.get(0);
        List<List> dataPoints = (List<List>) series.get("datapoints");
        long startSecond = new Date().getTime()/1000 - dataPoints.size();

        Double sum = 0.0d;
        Double min = 0.0d;
        Double max = 0.0d;
        for(int i=0; i<dataPoints.size();i++) {
            List dataPoint = dataPoints.get(i);
            dataPoint.set(1,startSecond);
            startSecond+=1;

            if(i == 0) {
                metrics.add(new Metric("first."+seriesName, dataPoint.get(0)));
                min = (Double) dataPoint.get(0);
                max = (Double) dataPoint.get(0);
            }
            if(i == dataPoints.size()-1) {
                metrics.add(new Metric("last."+seriesName, dataPoint.get(0)));
            }

            if(min > (Double) dataPoint.get(0))
                min = (Double) dataPoint.get(0);

            if(max < (Double) dataPoint.get(0))
                max = (Double) dataPoint.get(0);

            sum += (Double) dataPoint.get(0);
        }
        metrics.add(new Metric("sum."+seriesName, sum));
        metrics.add(new Metric("avg."+seriesName, sum/dataPoints.size()));
        metrics.add(new Metric("min."+seriesName, min));
        metrics.add(new Metric("max." + seriesName, max));
        return metrics;
    }

    private String resolveDescription(String description) {
        String resolvedDescription = "";
        for(Metric metric : metrics) {
            resolvedDescription = description.replace("$"+metric.getKey(), metric.getValue().toString());
        }
        return resolvedDescription;
    }


}
