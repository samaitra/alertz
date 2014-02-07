package com.flipkart.alert.domain;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 29/04/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class OnDemandRule extends Rule{

    public static List<OnDemandRule> getRules(String teamName, String metricName) {
        List<OnDemandRule> allRules = getAll(OnDemandRule.class);

        // Just a Quick Solution
        List<OnDemandRule> matchingRules = new ArrayList<OnDemandRule>();
        for(OnDemandRule rule : allRules) {
            if(rule.getTeam().contains(teamName)) {
                for(RuleCheck check : rule.getChecks()) {
                    if(check.getBooleanExpression().contains(metricName)) {
                        matchingRules.add(rule);
                        break;
                    }
                }
            }
        }

        return matchingRules;

    }

    public void update(OnDemandRule withRule) {
        Session session = beginTransaction();
        this.deleteChecks();
        this.deleteVariables();
        this.deleteEndPoints();
        withRule.setRuleId(getRuleId());
        withRule.update();
        commitTransaction(session);
    }

    public static List<String> getTeams() {
        Session session = beginTransaction();
        List<String> teams = session.createSQLQuery("select distinct(TEAM) from RULES where IS_SCHEDULED=0").list();
        commitTransaction(session);
        return teams;
    }

}
