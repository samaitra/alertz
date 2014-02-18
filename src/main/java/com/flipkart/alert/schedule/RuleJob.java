package com.flipkart.alert.schedule;

import com.flipkart.alert.dispatch.StatusDispatchService;
import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.*;
import com.flipkart.alert.storage.SourceClient;
import com.flipkart.alert.util.MetricHelper;
import com.flipkart.alert.util.RuleHelper;
import org.quartz.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.flipkart.alert.storage.MetricSourceClientFactory.clientFactory;

/**
 * User: nitinka
 */
public class RuleJob implements Job {
    private static List<String> runningJobs;
    private static Log log = Log.forClass(RuleJob.class);
    static {
        log = Log.forClass(RuleJob.class);
        runningJobs = new ArrayList<String>();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        ScheduledRule rule = getRule(jobDataMap);

        if(rule != null) {
            long startTime = System.currentTimeMillis();
            try {
                jobStarted(rule.getName());
                if(rule.canRunNow()) {
                    Alert alert = runRule(rule);
                    StatusDispatchService.dispatch(rule, alert);
                }
            } catch (ParseException e) {
                throw new JobExecutionException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                jobEnded(rule.getName(), startTime);
            }
        }
    }

    private void jobEnded(String name, long startTime) {
        log.info("Rule Completed : "+name+". Time To Complete: "+(System.currentTimeMillis() - startTime));
        synchronized (runningJobs) {
            runningJobs.remove(name);
            log.info("Running Jobs :"+runningJobs + " : "+runningJobs.size());
        }
    }

    private void jobStarted(String name) {
        log.info("Running Rule "+name);
        synchronized (runningJobs) {
            runningJobs.add(name);
            log.info("Running Jobs :"+runningJobs + " : "+runningJobs.size());
        }
    }

    public Alert runRule(ScheduledRule rule) throws JobExecutionException, InterruptedException, IOException {
        List<Metric> metrics = getMetrics(rule.getDataSerieses());
        if(metrics.size()==0){
            RuleHelper.markRuleAsTriggered(rule);
            return null;
        }
        Set<MetricTag> tags = MetricHelper.fetchScheduledRuleTags(rule.getTeam());

        Set<AlertVariable> variables = rule.getVariables();
        metrics.addAll(MetricHelper.resolveVariablesAndCreateMetrics(variables, metrics));

        return RuleHelper.runChecks(rule, metrics, tags);
    }

    private ScheduledRule getRule(JobDataMap jobDataMap) {
        Long ruleId = (Long) jobDataMap.get("ruleId");
        return ScheduledRule.getById(ScheduledRule.class, ruleId);
    }

    private synchronized List<Metric> getMetrics(Set<DataSeries> dataSeriesSet) throws JobExecutionException{

        List<Metric> metrics = new ArrayList<Metric>();
        SourceClient sourceClient = null;

        for(DataSeries seriesInfo : dataSeriesSet) {

            String sourceName = seriesInfo.getSource();
            try {
                clientFactory().prepareClientForSource(sourceName);
                sourceClient = clientFactory().getClient(sourceName);

                sourceClient.setQuery(seriesInfo.getQuery());
                sourceClient.setQueryName(seriesInfo.getName());

                metrics.addAll(sourceClient.execute());

            } catch (ClassNotFoundException e) {
                throw new JobExecutionException(e);
            } catch (InvocationTargetException e) {
                throw new JobExecutionException(e);
            } catch (NoSuchMethodException e) {
                throw new JobExecutionException(e);
            } catch (InstantiationException e) {
                throw new JobExecutionException(e);
            } catch (IllegalAccessException e) {
                throw new JobExecutionException(e);
            } catch (ParseException e) {
                throw new JobExecutionException(e);
            } catch (IOException e) {
                throw new JobExecutionException(e);
            } catch (InterruptedException e) {
                throw new JobExecutionException(e);
            }
        }

        return metrics;
    }



}
