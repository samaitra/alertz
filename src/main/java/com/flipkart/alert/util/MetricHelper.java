package com.flipkart.alert.util;

import com.flipkart.alert.domain.*;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 15/05/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricHelper {

    private static final Pattern PATTERN_REGEX_VARIABLE = Pattern.compile(".*\\$\\{(.+)\\}.*");

    public synchronized static List<Metric> fetchPredefinedStatistics(DescriptiveStatistics statistics, String queryName) {
        List<Metric> metrics = new ArrayList<Metric>();
        if (statistics.getN()!=0) {
            int lastElementIndex = (int) (statistics.getN() - 1);

            metrics.add(new Metric("first" + "." + queryName, statistics.getElement(0)));
            metrics.add(new Metric("last" + "." + queryName, statistics.getElement(lastElementIndex)));
            metrics.add(new Metric("min" + "." + queryName, statistics.getMin()));
            metrics.add(new Metric("max" + "." + queryName, statistics.getMax()));
            metrics.add(new Metric("count" + "." + queryName, statistics.getN()));
            metrics.add(new Metric("avg" + "." + queryName, statistics.getMean()));
            metrics.add(new Metric("stdDev" + "." + queryName, statistics.getStandardDeviation()));
            metrics.add(new Metric("sum" + "." + queryName, statistics.getSum()));
            metrics.add(new Metric("75thPercentile" + "." + queryName, statistics.getPercentile(75)));
            metrics.add(new Metric("95thPercentile" + "." + queryName, statistics.getPercentile(95)));
            metrics.add(new Metric("99thPercentile" + "." + queryName, statistics.getPercentile(99)));
            metrics.add(new Metric("median" + "." + queryName, statistics.getPercentile(50)));
        }
        return  metrics;
    }

    public synchronized static List<Metric> fetchPredefinedStatistics(String prefix, DescriptiveStatistics statistics, String queryName) {
        List<Metric> metrics = new ArrayList<Metric>();
        if (statistics.getN()!=0) {
            int lastElementIndex = (int) (statistics.getN() - 1);
            if(prefix == null || prefix.trim().equals("")) {
                prefix = "";
            }

            metrics.add(new Metric(prefix + "first" + "." + queryName, statistics.getElement(0)));
            metrics.add(new Metric(prefix + "last" + "." + queryName, statistics.getElement(lastElementIndex)));
            metrics.add(new Metric(prefix + "min" + "." + queryName, statistics.getMin()));
            metrics.add(new Metric(prefix + "max" + "." + queryName, statistics.getMax()));
            metrics.add(new Metric(prefix + "count" + "." + queryName, statistics.getN()));
            metrics.add(new Metric(prefix + "avg" + "." + queryName, statistics.getMean()));
            metrics.add(new Metric(prefix + "stdDev" + "." + queryName, statistics.getStandardDeviation()));
            metrics.add(new Metric(prefix + "sum" + "." + queryName, statistics.getSum()));
            metrics.add(new Metric(prefix + "75thPercentile" + "." + queryName, statistics.getPercentile(75)));
            metrics.add(new Metric(prefix + "95thPercentile" + "." + queryName, statistics.getPercentile(95)));
            metrics.add(new Metric(prefix + "99thPercentile" + "." + queryName, statistics.getPercentile(99)));
            metrics.add(new Metric(prefix + "median" + "." + queryName, statistics.getPercentile(50)));
        }
        return  metrics;
    }

    public static List<Metric> resolveVariablesAndCreateMetrics(Set<AlertVariable> variables, List<Metric> metrics) {
        HashMap<String, String> variableMap = new HashMap<String, String>();
        List<Metric> newMetricList = new ArrayList<Metric>();

        for(AlertVariable variable : variables) {
            variableMap.put(variable.getName(), variable.getValue());
        }

        for(AlertVariable variable : variables) {
            for(String key : variableMap.keySet()){
                variable.setValue(variable.getValue().replace("$" +key, variableMap.get(key)));
            }
            for(Metric metric : metrics){
                String expression = variable.getValue().replace("$" + metric.getKey(), metric.getValue().toString());
                variable.setValue(expression);
            }

            Matcher matcher = PATTERN_REGEX_VARIABLE.matcher(variable.getValue());
            if(matcher.matches()) {
                String regExVar = matcher.group(1);
                Pattern variablePattern = Pattern.compile(regExVar);
                for(Metric metric : metrics) {
                    Matcher variableMatcher = variablePattern.matcher(metric.getKey());
                    if(variableMatcher.matches()) {
                        String variableValue = variable.getValue().replace("${"+regExVar+"}", String.valueOf(metric.getValue()));
                        String variableEvaluatedValue = ExpressionHelper.evaluateExpression(variableValue).toString();

                        Metric newMetric = new Metric(metric.getKey() + "." + variable.getName(), variableEvaluatedValue);
                        newMetric.addTag(new MetricTag("variableKey", metric.getKey()));
                        newMetricList.add(newMetric);
                    }
                }
            }
            else {
                variable.setValue(ExpressionHelper.evaluateExpression(variable.getValue()).toString());
                Metric newMetric = new Metric(variable.getName(), variable.getValue());
                newMetricList.add(newMetric);
            }
        }
        return newMetricList;
    }

    public static Set<MetricTag> fetchScheduledRuleTags(String teamName) {
        Set<MetricTag> tags = new HashSet<MetricTag>();
        tags.add(new MetricTag("teamName", teamName));
        return tags;
    }

    public static Set<MetricTag> fetchOnDemandRuleTags(String teamName, OnDemandRuleEvent event) {
        Set<MetricTag> tags = new HashSet<MetricTag>();
        tags.add(new MetricTag("teamName", teamName));
        tags.add(new MetricTag("hostName", event.getHostName()));
        tags.add(new MetricTag("appName", event.getAppName()));
        tags.add(new MetricTag("apiName", event.getApiName()));
        return tags;
    }

    public static String valueType(Object value) {
        try {
            Double.parseDouble(value.toString());
        }
        catch (NumberFormatException nfe) {
            return "STRING";
        }
        return "NUMBER";
    }
}
