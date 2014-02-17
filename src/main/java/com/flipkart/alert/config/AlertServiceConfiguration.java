package com.flipkart.alert.config;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 25/10/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */

import com.yammer.dropwizard.config.Configuration;

import java.util.Map;

public class AlertServiceConfiguration extends Configuration {
    private String appName;
    private Map<String,String> metricSourceClients;
    private StatusDispatcherServiceConfiguration statusDispatcherServiceConfiguration;
    private RuleEventsConfiguration ruleEventsConfiguration;
    private MetricArchiverConfiguration metricArchiverConfiguration;
    private GraphiteConfiguration graphiteConfiguration;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, String> getMetricSourceClients() {
        return metricSourceClients;
    }

    public void setMetricSourceClients(Map<String, String> metricSourceClients) {
        this.metricSourceClients = metricSourceClients;
    }

    public StatusDispatcherServiceConfiguration getStatusDispatcherServiceConfiguration() {
        return statusDispatcherServiceConfiguration;
    }

    public void setStatusDispatcherServiceConfiguration(StatusDispatcherServiceConfiguration configuration) {
        this.statusDispatcherServiceConfiguration = configuration;
    }


    public RuleEventsConfiguration getRuleEventsConfiguration() {
        return ruleEventsConfiguration;
    }

    public void setRuleEventsConfiguration(RuleEventsConfiguration ruleEventsConfiguration) {
        this.ruleEventsConfiguration = ruleEventsConfiguration;
    }

    public MetricArchiverConfiguration getMetricArchiverConfiguration() {
        return metricArchiverConfiguration;
    }

    public void setMetricArchiverConfiguration(MetricArchiverConfiguration metricArchiverConfiguration) {
        this.metricArchiverConfiguration = metricArchiverConfiguration;
    }

    public GraphiteConfiguration getGraphiteConfiguration() {
        return graphiteConfiguration;
    }

    public void setGraphiteConfiguration(GraphiteConfiguration graphiteConfiguration) {
        this.graphiteConfiguration = graphiteConfiguration;
    }
}
