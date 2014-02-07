package com.flipkart.alert.testHelper;

import com.sun.jersey.api.client.Client;
import com.flipkart.alert.domain.RuleStat;
import com.flipkart.alert.domain.ScheduledRule;

import java.io.IOException;
import java.util.List;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 17/12/12
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduledRuleResourceClient {

    private Client client;

    public ScheduledRuleResourceClient(Client client) {
        this.client = client;
    }

    public ScheduledRule createRule(String jsonRuleFile) throws IOException {
        return client.
                resource("/scheduledRules").
                header("content-type", "application/json").
                post(ScheduledRule.class,
                        jsonFixture(jsonRuleFile));
    }

    public ScheduledRule getRule(int ruleId) {
        return client.
                resource("/scheduledRules/" + ruleId).
                header("content-type", "application/json").
                get(ScheduledRule.class);
    }

    public List<RuleStat> getAllRuleStats() {
        return CollectionHelper.transformList(client.
                resource("/scheduledRules/latestStats").
                header("content-type", "application/json").
                get(List.class), RuleStat.class);
    }

    public ScheduledRule updateRule(Integer ruleId, String jsonRuleFile) throws IOException {
        return client.
                resource("/scheduledRules/"+ruleId).
                header("content-type", "application/json").
                put(ScheduledRule.class,
                        jsonFixture(jsonRuleFile));
    }

    public void deleteRule(Integer id) {
        client.
                resource("/scheduledRules/" + id).
                delete(Void.class);
    }

    public List<ScheduledRule> searchRules(String metricKey) {
        return CollectionHelper.transformList(client.resource("/scheduledRules?metricName=" + metricKey).get(List.class), ScheduledRule.class);
    }

}
