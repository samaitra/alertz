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
    private DispatcherConfiguration dispatcherConfiguration;
    private RuleEventsConfiguration ruleEventsConfiguration;
    private DataArchivalConfiguration dataArchivalConfiguration;
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

    public DispatcherConfiguration getDispatcherConfiguration() {
        return dispatcherConfiguration;
    }

    public void setDispatcherConfiguration(DispatcherConfiguration configuration) {
        this.dispatcherConfiguration = configuration;
    }


    public RuleEventsConfiguration getruleEventsConfiguration() {
        return ruleEventsConfiguration;
    }

    public void setRuleEventsConfiguration(RuleEventsConfiguration ruleEventsConfiguration) {
        this.ruleEventsConfiguration = ruleEventsConfiguration;
    }

    public DataArchivalConfiguration getDataArchivalConfiguration() {
        return dataArchivalConfiguration;
    }

    public void setDataArchivalConfiguration(DataArchivalConfiguration dataArchivalConfiguration) {
        this.dataArchivalConfiguration = dataArchivalConfiguration;
    }

    public GraphiteConfiguration getGraphiteConfiguration() {
        return graphiteConfiguration;
    }

    public void setGraphiteConfiguration(GraphiteConfiguration graphiteConfiguration) {
        this.graphiteConfiguration = graphiteConfiguration;
    }
}
