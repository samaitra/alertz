package flipkart.alert.schedule;

import com.yammer.dropwizard.logging.Log;
import flipkart.alert.domain.*;
import flipkart.alert.util.DateHelper;
import flipkart.alert.schedule.job.JobAlreadyExistsException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * User: nitinka
 * Date: 22/11/12
 * Time: 10:27 AM
 * This class exposes all apis required to do Quartz Job Scheduling management including :
 *   - starting
 *   - listing
 *   - pausing and
 *   - removing.
 * This class instantiate the Quartz Scheduler in static initialization phase.
 */
public class Scheduler{
    private static Scheduler scheduler;
    private org.quartz.Scheduler quartzScheduler;
    private Thread schedulerStopThread;
    private static Log log = Log.forClass(org.quartz.Scheduler.class);

    protected Scheduler() {
        try {
            quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
            quartzScheduler.start();
            log.info("Scheduler Started");

            schedulerStopThread = new Thread() {
                public void run() {
                    try {
                        log.info("Shutting Down Scheduler");
                        quartzScheduler.shutdown();
                        log.info("Scheduler Stopped");
                    } catch (SchedulerException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            };

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static Scheduler getScheduler() {
        if(scheduler == null) {
            scheduler = new Scheduler();
        }
        return scheduler;
    }

    public Thread schedulerStopThread() {
        return schedulerStopThread;
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException, JobAlreadyExistsException {
        if(quartzScheduler.checkExists(jobDetail.getKey()))
            throw  new JobAlreadyExistsException(jobDetail.getKey());
        quartzScheduler.scheduleJob(jobDetail, trigger);
    }

    public void removeJob(JobKey jobKey) throws SchedulerException{
        quartzScheduler.deleteJob(jobKey);
    }

    public void removeAllJobs() throws SchedulerException {
        for (String groupName : quartzScheduler.getJobGroupNames()) {
            for (JobKey jobKey : quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                quartzScheduler.deleteJob(jobKey);
            }
        }
    }

    public List<Map<String,String>> listAllJobs() throws SchedulerException {
        List<Map<String,String>> jobs = new ArrayList<Map<String, String>>();

        for (String groupName : quartzScheduler.getJobGroupNames()) {
            for (JobKey jobKey : quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobs.add(getJobDetails(jobKey.getName(), groupName));
            }
        }
        return jobs;
    }

    public Map<String, String> getJobDetails(String jobId, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId, groupName);
        return jobDetailToMap(quartzScheduler.getJobDetail(jobKey),
                quartzScheduler.getTrigger(new TriggerKey(jobId, groupName)));
    }

    private Map<String, String> jobDetailToMap(JobDetail jobDetail, Trigger jobTrigger) throws SchedulerException {
        Map<String,String> jobInfoMap = new HashMap<String, String>();
        if(jobDetail != null) {
            jobInfoMap.put("status",this.quartzScheduler.getTriggerState(jobTrigger.getKey()).toString());
            jobInfoMap.put("jobClass",jobDetail.getJobClass().getCanonicalName());
            if(jobTrigger.getPreviousFireTime() != null)
                jobInfoMap.put("previousFireTime",jobTrigger.getPreviousFireTime().toString());
            jobInfoMap.put("nextFireTime",jobTrigger.getNextFireTime().toString());
            jobInfoMap.put("description",jobDetail.getDescription());
        }
        return jobInfoMap;
    }

    public void pauseJob(JobKey jobKey) throws SchedulerException {
        quartzScheduler.pauseJob(jobKey);
    }

    public void resumeJob(JobKey jobKey) throws SchedulerException {
        quartzScheduler.resumeJob(jobKey);
    }

    public Trigger buildTrigger(ScheduledRule scheduledRule) throws ParseException {
        RuleSchedule jobSchedule = scheduledRule.getSchedule();
        SimpleScheduleBuilder scheduleBuilder = simpleSchedule().
                repeatForever().
                withIntervalInMilliseconds(DateHelper.computeIntervalInMilliSeconds(jobSchedule.getInterval())).
                withMisfireHandlingInstructionFireNow();

        TriggerBuilder triggerBuilder = newTrigger().
                withIdentity(new TriggerKey(scheduledRule.getName(), scheduledRule.getTeam())).
                startNow().
                withSchedule(scheduleBuilder);

        if(jobSchedule.getStartDate()!=null) {
            triggerBuilder.startAt(jobSchedule.getStartDate());
        }

        if(jobSchedule.getEndDate()!=null) {
            triggerBuilder.endAt(jobSchedule.getEndDate());
        }

        return triggerBuilder.build();
    }


    public String buildTriggerName(String metricAlias, String duration, String interval) {
        return String.format("Trigger.%s.%s.%s",metricAlias,duration,interval);
    }
}
