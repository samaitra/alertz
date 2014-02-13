
package com.flipkart.alert.resource;

import com.flipkart.alert.schedule.RuleJob;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.annotation.Timed;
import com.flipkart.alert.domain.Alert;
import com.flipkart.alert.domain.Rule;
import com.flipkart.alert.domain.RuleStat;
import com.flipkart.alert.domain.ScheduledRule;
import com.flipkart.alert.schedule.Scheduler;
import com.flipkart.alert.exception.JobAlreadyExistsException;
import com.flipkart.alert.util.NumberHelper;
import com.flipkart.alert.util.ResponseBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/10/12
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/scheduledRules")
public class ScheduledRuleResource {
    private static Log log = Log.forClass(ScheduledRuleResource.class);
    private Scheduler scheduler = Scheduler.getScheduler();
    /**
     * Create Rule and Return Created Rule as Response
     * POST on /rules
     * Request Body :
     {
         "name" : "r1",
         "team" : "t1",
         "dataSerieses":
                 [
                     {
                         "name": "series1",
                         "source": "w3.graphite",
                         "query": "from=-2hours&until=now&height=750&title=METRIC_2_Unavailability_Ratio_percentage&target=legendValue%28alias%28asPercent%28divideSeries%28summarize%28statsd.w3.website.core_metrics.OOSProduct._all.Count.per_min%2C%2210min%22%29%2Csummarize%28statsd.w3.website.core_metrics.pageView._all.Count.per_min%2C%2210min%22%29%29%2C1%29%2C%22OOS%2BDiscontinued%20To%20Total%20PV%3A%20Last%20Value%20%28%25%29%3A%20%22%29%2C%22last%22%29&uniq=0.13684863388497093&format=json"
                     }
                 ],
         "checks":
                 [
                     {
                         "description" :"desc1",
                         "booleanExpression": "$last.series1 < $avg.series1",
                         "alertLevel": "1"
                     }

                 ],
         "schedule": {
             "interval": "1m",
             "startDate":"2012-11-10",
             "endDate":"2015-12-15",
             "days":"WEEKDAY",
             "times":"10:00:00-22:00:00,23:00:00-23:59:59",
             "dates" : "2012/11/11-2012/11/30,2012/12/01-2012/12/14"
         }
     }
     **/

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Timed
    public ScheduledRule createRule(@Valid ScheduledRule rule){
        rule.create();
        scheduleJob(rule);
        return rule;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{ruleId}")
    public ScheduledRule getRule(@PathParam("ruleId") String ruleId) throws Exception {
        return searchRule(ruleId);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<ScheduledRule> getRules(@QueryParam("teamName") @DefaultValue("") String teamName,
                                        @QueryParam("metricName") @DefaultValue("") String metricName) throws Exception {
        return ScheduledRule.getRules(teamName, metricName);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/names")
    public List getRuleNames(@QueryParam("startsWith") String startsWith) throws Exception {
        return Rule.getAllNames(startsWith, ScheduledRule.class);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{ruleId}")
    public ScheduledRule updateRule(@PathParam("ruleId")String ruleId, @Valid ScheduledRule withRule) throws Exception {
        ScheduledRule searchedRule = searchRule(ruleId);
        String oldRuleName = searchedRule.getName();
        String oldTeamName = searchedRule.getTeam();
        searchedRule.update(withRule);
        scheduler.removeJob(new JobKey(oldRuleName,oldTeamName));
        scheduleJob(withRule);
        return withRule;
    }

    @DELETE
    @Timed
    @Path("/{ruleId}")
    public void deleteRule(@PathParam("ruleId") String ruleId) throws Exception {
        ScheduledRule searchedRule = searchRule(ruleId);
        searchedRule.delete();
        log.info("Removing job using Name :"+searchedRule.getName()+" and group :"+searchedRule.getTeam());
        scheduler.removeJob(new JobKey(searchedRule.getName(),
                searchedRule.getTeam()));
    }

    @Path("/{ruleId}/forceRun")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Timed
    public Object forceRun(@PathParam("ruleId") String ruleId) throws Exception {
        ScheduledRule searchedRule = searchRule(ruleId);
        Alert alert = new RuleJob().runRule(searchedRule);
        if(alert == null) return searchedRule;
        return alert;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{ruleId}/status")
    public Map getRuleStatus(@PathParam("ruleId") String ruleId) throws Exception {
        ScheduledRule rule = searchRule(ruleId);
        return scheduler.getJobDetails(rule.getName(), rule.getTeam());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{ruleId}/latestStats")
    public RuleStat getLatestRuleStats(@PathParam("ruleId") String ruleId) throws Exception {
        return searchStat(ruleId);
    }

    @Path("/latestStats")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<RuleStat> getLatestForAll() {
        return RuleStat.getAllStats();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{ruleId}/pause")
    public Map pauseRule(@PathParam("ruleId") String ruleId) throws Exception {
        ScheduledRule rule = searchRule(ruleId);
        scheduler.pauseJob(new JobKey(rule.getName(), rule.getTeam()));
        return scheduler.getJobDetails(rule.getName(), rule.getTeam());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{ruleId}/resume")
    public Map resumeRule(@PathParam("ruleId") String ruleId) throws Exception {
        ScheduledRule rule = searchRule(ruleId);
        scheduler.resumeJob(new JobKey(rule.getName(), rule.getTeam()));
        return scheduler.getJobDetails(rule.getName(), rule.getTeam());
    }

    private JobDetail buildJob(ScheduledRule rule) {
        return newJob(RuleJob.class).
                withIdentity(rule.getName(), rule.getTeam()).
                usingJobData("ruleId", rule.getRuleId()).
                build();
    }

    private ScheduledRule searchRule(String ruleId) {
        if(NumberHelper.isNumber(ruleId)) {
            return ScheduledRule.getById(ScheduledRule.class, Long.parseLong(ruleId));
        }
        List<ScheduledRule> rules = ScheduledRule.getByColumn(ScheduledRule.class,"name",ruleId);
        ScheduledRule searchedRule = rules.size() > 0 ? rules.get(0) : null;

        if(searchedRule != null) {
            return searchedRule;
        }
        throw new WebApplicationException(ResponseBuilder.notFound("Rule With id "+ruleId+" not found"));
    }

    private RuleStat searchStat(String ruleId) {
        if(NumberHelper.isNumber(ruleId)) {
            RuleStat ruleStat = RuleStat.getById(RuleStat.class, Long.parseLong(ruleId));
            if(ruleStat!=null) {
                ruleStat.setRuleId(Long.parseLong(ruleId));
                return ruleStat;
            }
        }
        throw new WebApplicationException(ResponseBuilder.notFound("Rule With id "+ruleId+" not found"));
    }

    private void scheduleJob(ScheduledRule rule){
        Trigger trigger = null;
        try {
            trigger = scheduler.buildTrigger(rule);
            JobDetail job = buildJob(rule);
            scheduler.scheduleJob(job, trigger);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        } catch (JobAlreadyExistsException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseBuilder.duplicate("Quartz Job Already Exist"));
        }
    }
}
