package com.flipkart.alert.domain;

import com.flipkart.alert.dispatch.StatusDispatchService;
import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.util.MetricHelper;
import com.flipkart.alert.util.RuleHelper;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 29/4/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Setter @Getter
public class OnDemandRuleEvent {
    private static Log log = Log.forClass(OnDemandRuleEvent.class);

    private String hostName;

    private String appName;

    private String apiName;

    private HashMap<String, Long> data;

    @JsonIgnore
    private List<Metric> metrics;

    public String toString() {
        return "Host Name:" + hostName +"App Name: " + appName + " API name: " + apiName + " Metrics: " + metrics;
    }

    public OnDemandRuleEvent execute() {
        String ruleName = appName + "-" + apiName;
        List<OnDemandRule> rules;
        if(apiName.equals("*"))
            rules = OnDemandRule.getByColumnMatcher(OnDemandRule.class, "name", appName + "%");
        else
            rules = OnDemandRule.getByColumn(OnDemandRule.class, "name", ruleName);

        log.info("Posted with data: " + " Host Name: " + hostName +" App Name: " + appName + " API name: " + apiName + " Data: " + data);
        try {
            for(OnDemandRule rule: rules) {
                runRule(rule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    private void runRule(OnDemandRule rule) throws Exception {
        metrics = buildMetricsFromDataMap(data);
        metrics.addAll(MetricHelper.resolveVariablesAndCreateMetrics(rule.getVariables(), metrics));
        Set<MetricTag> tags = MetricHelper.fetchOnDemandRuleTags(rule.getTeam(), this);

        Alert alert = RuleHelper.runChecks(rule, metrics, tags);
        StatusDispatchService.dispatch(rule, alert);
    }

    private List<Metric> buildMetricsFromDataMap(Map<String, Long> data) {
        List<Metric> metrics = new ArrayList<Metric>();
        for(Map.Entry<String,Long> entry: data.entrySet()) {
            Metric newMetric = new Metric();
            newMetric.setKey(entry.getKey());
            newMetric.setValue(entry.getValue());
            metrics.add(newMetric);
        }
        return metrics;
    }

}
