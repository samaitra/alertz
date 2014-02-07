package com.flipkart.alert.resource;

import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.annotation.Timed;
import com.flipkart.alert.domain.OnDemandRule;
import com.flipkart.alert.domain.RuleStat;
import com.flipkart.alert.domain.ScheduledRule;
import com.flipkart.alert.schedule.Scheduler;
import com.flipkart.alert.util.NumberHelper;
import com.flipkart.alert.util.ResponseBuilder;
import org.quartz.JobKey;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/10/12
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/teams")
public class TeamResource {
    private static Log log = Log.forClass(TeamResource.class);
    private Scheduler scheduler = Scheduler.getScheduler();

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/scheduledRules")
    public List<String> getScheduledRulesTeams() throws Exception {
        return ScheduledRule.getTeams();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/onDemandRules")
    public List<String> getOnDemandRulesTeams() throws Exception {
        return OnDemandRule.getTeams();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{teamName}/scheduledRules")
    public List<ScheduledRule> getTeamScheduledRules(@PathParam("teamName") String teamName) throws Exception {
        return ScheduledRule.getRules(teamName, "");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{teamName}/onDemandRules")
    public List<OnDemandRule> getTeamOnDemandRules(@PathParam("teamName") String teamName) throws Exception {
        return OnDemandRule.getRules(teamName, "");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{teamName}/scheduledRules/stats")
    public List<RuleStat> getScheduledRuleStats(@PathParam("teamName") String teamName) throws Exception {
        return RuleStat.getStatsForTeam(teamName, ScheduledRule.class);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{teamName}/onDemandRules/stats")
    public List<RuleStat> getOnDemandRuleStats(@PathParam("teamName") String teamName) throws Exception {
        return RuleStat.getStatsForTeam(teamName, OnDemandRule.class);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{teamName}/pauseRules")
    public List<Map<String, String>> pauseAllRules(@PathParam("teamName") String teamName) throws Exception {
        List<ScheduledRule> rules = ScheduledRule.getRules(teamName, "");
        List<Map<String, String>> jobDetails = new ArrayList<Map<String, String>>();
        for (ScheduledRule rule : rules) {
            scheduler.pauseJob(new JobKey(rule.getName(), rule.getTeam()));
            jobDetails.add(scheduler.getJobDetails(rule.getName(), rule.getTeam()));
        }
        return jobDetails;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{teamName}/resumeRules")
    public List<Map<String, String>> resumeAllRules(@PathParam("teamName") String teamName) throws Exception {
        List<ScheduledRule> rules = ScheduledRule.getRules(teamName, "");
        List<Map<String, String>> jobDetails = new ArrayList<Map<String, String>>();
        for (ScheduledRule rule : rules) {
            scheduler.resumeJob(new JobKey(rule.getName(), rule.getTeam()));
            jobDetails.add(scheduler.getJobDetails(rule.getName(), rule.getTeam()));
        }
        return jobDetails;
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{teamName}/allJobDetails")
    public List<Map<String, String>> getRuleStatusForTeam(@PathParam("teamName") String teamName) throws Exception {
        List<ScheduledRule> rules = ScheduledRule.getRules(teamName, "");
        List<Map<String, String>> allJobDetails = new ArrayList<Map<String, String>>();

        for (ScheduledRule rule : rules) {
            ScheduledRule r = searchRule(String.valueOf(rule.getRuleId()));
            allJobDetails.add(scheduler.getJobDetails(r.getName(), r.getTeam()));
        }
        return allJobDetails;
    }

    private ScheduledRule searchRule(String ruleId) {
        if (NumberHelper.isNumber(ruleId)) {
            return ScheduledRule.getById(ScheduledRule.class, Long.parseLong(ruleId));
        }
        List<ScheduledRule> rules = ScheduledRule.getByColumn(ScheduledRule.class, "name", ruleId);
        ScheduledRule searchedRule = rules.size() > 0 ? rules.get(0) : null;

        if (searchedRule != null) {
            return searchedRule;
        }
        throw new WebApplicationException(ResponseBuilder.notFound("Rule With id " + ruleId + " not found"));
    }

}
