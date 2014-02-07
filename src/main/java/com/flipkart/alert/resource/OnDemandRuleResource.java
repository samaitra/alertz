package com.flipkart.alert.resource;

import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.annotation.Timed;
import com.flipkart.alert.domain.OnDemandRule;
import com.flipkart.alert.domain.OnDemandRuleEvent;
import com.flipkart.alert.domain.Rule;
import com.flipkart.alert.domain.RuleStat;
import com.flipkart.alert.storage.RuleEventsFactory;
import com.flipkart.alert.util.NumberHelper;
import com.flipkart.alert.util.ResponseBuilder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 26/4/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/onDemandRules")
public class OnDemandRuleResource {
    private static Log log = Log.forClass(OnDemandRuleResource.class);

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public OnDemandRule createRule(@Valid OnDemandRule rule) throws Exception {
        rule.create();
        return rule;
    }


    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/events")
    public List<OnDemandRuleEvent> postEvents(List<OnDemandRuleEvent> events) throws Exception {
        RuleEventsFactory.addEvents(events);
        log.info("Processing " + events.size() + " events(s)");
        return events;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Timed
    @Path("/{ruleId}")
    public OnDemandRule updateRule(@PathParam("ruleId")String ruleId, @Valid OnDemandRule withRule) throws Exception {
        OnDemandRule searchedRule = searchRule(ruleId);
        String oldRuleName = searchedRule.getName();
        String oldTeamName = searchedRule.getTeam();
        searchedRule.update(withRule);
        return withRule;
    }

    @DELETE
    @Timed
    @Path("/{ruleId}")
    public void deleteRule(@PathParam("ruleId") String ruleId) throws Exception {
        OnDemandRule searchedRule = searchRule(ruleId);
        searchedRule.delete();
        log.info("Removing job using Name :"+searchedRule.getName()+" and group :"+searchedRule.getTeam());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<OnDemandRule> getRules(@QueryParam("teamName") @DefaultValue("") String teamName,
                                        @QueryParam("metricName") @DefaultValue("") String metricName) throws Exception {
        return OnDemandRule.getRules(teamName, metricName);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{ruleId}")
    public OnDemandRule getRule(@PathParam("ruleId") String ruleId) throws Exception {
        return searchRule(ruleId);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{ruleId}/latestStats")
    public RuleStat getRuleStats(@PathParam("ruleId") String ruleId) throws Exception {
        return searchStat(ruleId);
    }

    @Path("/latestStats")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<RuleStat> getAll() {
        return RuleStat.getAllStats();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/names")
    public List getRuleNames(@QueryParam("startsWith") String startsWith) throws Exception {
        return Rule.getAllNames(startsWith, OnDemandRule.class);
    }

    private OnDemandRule searchRule(String ruleId) {
        if(NumberHelper.isNumber(ruleId)) {
            return OnDemandRule.getById(OnDemandRule.class, Long.parseLong(ruleId));
        }
        List<OnDemandRule> rules = OnDemandRule.getByColumn(OnDemandRule.class,"name",ruleId);
        OnDemandRule searchedRule = rules.size() > 0 ? rules.get(0) : null;

        if(searchedRule != null) {
            return searchedRule;
        }
        throw new WebApplicationException(ResponseBuilder.notFound("Rule With id " + ruleId + " not found"));
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


}
