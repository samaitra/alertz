package com.flipkart.alert.util;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.dispatch.StatusDispatchPipeline;
import com.flipkart.alert.domain.*;
import com.flipkart.alert.storage.OpenTsdbClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 01/05/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class RuleHelper {

    private static final Pattern PATTERN_REGEX_VARIABLE = Pattern.compile(".*\\$\\{(.+)\\}.*");
    private static Log log = Log.forClass(RuleHelper.class);

    static {
        log = Log.forClass(RuleHelper.class);
    }

    public static void publishAlert(Alert alert) throws Exception {
        try {
            if(!alert.getRule().getEndPoints().isEmpty()) {
                StatusDispatchPipeline.publishToQueue(alert, alert.getRule().getEndPoints());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void publishStatus(Rule rule) throws Exception {
        try {
            if(!rule.getEndPoints().isEmpty()) {
                StatusDispatchPipeline.publishToQueue(rule, rule.getEndPoints());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Alert runChecks(Rule rule, List<Metric> metrics, Set<MetricTag> tags) {
        Set<CheckStat> checkStats = new HashSet<CheckStat>();
        List<RuleCheck> breachedChecks = new ArrayList<RuleCheck>();
        List<Metric> metricsUsed = new ArrayList<Metric>();
        Metric ruleBreachMetric = new Metric.MetricBuilder().withKey(Metric.BREACHED).withMetricTags(tags).build();

        RuleStat ruleStatistic = new RuleStat.RuleStatBuilder().withRuleId(rule.getRuleId()).build();

        for(RuleCheck check : rule.getChecks()) {
            String booleanExpression = check.getBooleanExpression();
            String description = check.getDescription();

            for(Metric metric : metrics) {
                if(booleanExpression.contains("$"+metric.getKey())) {
                    String valueType = MetricHelper.valueType(metric.getValue());
                    if(valueType.equals("STRING")) {
                        booleanExpression = booleanExpression.replace("$"+metric.getKey(), "'" + String.valueOf(metric.getValue()) + "'");
                    }
                    else {
                        booleanExpression = booleanExpression.replace("$"+metric.getKey(), String.valueOf(metric.getValue()));
                        metricsUsed.add(metric);
                    }
                    booleanExpression = booleanExpression.replace("\\\"","'").replace("\"","'");
                }
                if(description.contains("$"+metric.getKey())) {
                    String valueType = MetricHelper.valueType(metric.getValue());
                    if(valueType.equals("STRING")) {
                        description = description.replace("$"+metric.getKey(), "'" + String.valueOf(metric.getValue()) + "'");
                    }
                    else {
                        description = description.replace("$"+metric.getKey(), String.valueOf(metric.getValue()));
                        if(!metricsUsed.contains(metric)) {
                            metricsUsed.add(metric);
                        }
                    }
                    description = description.replace("\\\"","'").replace("\"","'");
                }

                metric.addTags(tags);
            }

            CheckStat checkStat = new CheckStat.CheckStatBuilder().
                    withCheckId(check.getCheckId()).
                    withRuleStat(ruleStatistic).
                    build();

            Matcher matcher = PATTERN_REGEX_VARIABLE.matcher(booleanExpression);
            if(matcher.matches()) {
                String regExVar = matcher.group(1);
                Pattern variablePattern = Pattern.compile(regExVar);
                int ruleBreachCounter = 0;
                for(Metric metric : metrics) {
                    Matcher variableMatcher = variablePattern.matcher(metric.getKey());
                    String variableExpr = "${" + regExVar + "}"; // Used mostly for replacement

                    if(variableMatcher.matches()) {
                        Metric metricUsed = new Metric.MetricBuilder().
                                withKey(metric.getKey()).
                                withValue(metric.getValue()).
                                withMetricTags(metric.getMetricTags()).build();

                        for(MetricTag metricTag : metric.getMetricTags()) {
                            if(metricTag.getTag().equals("variableKey")) {
                                metricUsed.setKey(metric.getKey().replace(metricTag.getValue()+".",""));
                            }
                        }

                        if(!MetricHelper.valueType(metricUsed.getValue()).equals("STRING"))
                            metricsUsed.add(metricUsed);

                        String newBooleanExpression = booleanExpression.replace(variableExpr, String.valueOf(metric.getValue()));

                        if((Boolean) ExpressionHelper.evaluateExpression(newBooleanExpression)) {
                            checkStat.setBreached(true);
                            ruleStatistic.setBreached(true);
                            RuleCheck breachedCheck = new RuleCheck().
                                    setAlertLevel(check.getAlertLevel()).
                                    setBooleanExpression(check.getBooleanExpression().replace(variableExpr, String.valueOf(metric.getKey()))).
                                    setDescription(check.getDescription().replace(variableExpr, String.valueOf(metric.getValue()))).
                                    setRule(check.getRule());
                            breachedCheck.setCheckId(check.getCheckId());
                            breachedChecks.add(breachedCheck);
                            ruleBreachCounter++;

                            if(ruleBreachCounter == 1) {
                                checkStat.setCheckId(check.getCheckId());
                                checkStat.setExpression(check.getBooleanExpression().replace(variableExpr, String.valueOf(metric.getKey())));
                                checkStat.setDescription(description.replace(variableExpr, String.valueOf(metric.getValue())));
                            }

                        }
                        checkStats.add(checkStat);
                    }
                    if(ruleBreachCounter == 0) {
                        checkStat.setExpression(check.getBooleanExpression().replace(variableExpr, String.valueOf(metric.getKey())));
                        checkStat.setDescription(description.replace(variableExpr, String.valueOf(metric.getValue())));
                    }
                }
                ruleBreachMetric.setValue(ruleBreachCounter);
            }
            else {
                checkStat.setDescription(description);
                checkStat.setExpression(check.getBooleanExpression());

                if((Boolean) ExpressionHelper.evaluateExpression(booleanExpression)) {
                    checkStat.setBreached(true);
                    ruleStatistic.setBreached(true);
                    breachedChecks.add(check);
                    ruleBreachMetric.setValue(1);
                } else {
                    ruleBreachMetric.setValue(0);
                }
                checkStats.add(checkStat);
            }
        }
        ruleStatistic.setCheckStats(checkStats);
        ruleStatistic.create();
        metricsUsed.add(ruleBreachMetric);
        OpenTsdbClient.INSTANCE.pushMetrics(metricsUsed, rule.getName());

        if (breachedChecks.size() != 0 ) {
            log.info("Rule: " + rule.getName() + " with metrics " + metrics + "\nStatus: Breached");
            return new Alert(metricsUsed, rule, breachedChecks);
        }

        log.info("Rule: " + rule.getName() + " with metrics " + metrics + "\nStatus: OK");
        return null;
    }

    public static void markRuleAsTriggered(ScheduledRule rule) {
        log.warn("No Metrics Fetched From DataSource. Ignoring Checks and Marking " + rule.getName() +" as Triggered");
        Set<CheckStat> checkStats = new HashSet<CheckStat>();
        RuleStat ruleStatistic = new RuleStat.RuleStatBuilder()
                .withRuleId(rule.getRuleId())
                .withLastCheckTime(new Date())
                .build();

        for(RuleCheck check : rule.getChecks()) {
            checkStats.add(new CheckStat.CheckStatBuilder().
                    withCheckId(check.getCheckId()).
                    withRuleStat(ruleStatistic).
                    withExpression(check.getBooleanExpression()).
                    withDescription("No Data Found In This Run. Check was not run.")
                    .withBreached(false)
                    .build());
        }

        ruleStatistic.setCheckStats(checkStats);
        ruleStatistic.setBreached(false);
        ruleStatistic.create();
    }
}
