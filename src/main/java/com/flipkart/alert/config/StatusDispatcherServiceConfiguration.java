package com.flipkart.alert.config;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatcherServiceConfiguration {
    private int queueSize;
    private HashMap<String, StatusDispatcherConfig> targetConfigs;
    private int poolSize;

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public HashMap<String, StatusDispatcherConfig> getTargetConfigs() {
        return targetConfigs;
    }

    public void setTargetConfigs(HashMap<String, StatusDispatcherConfig> targetConfigs) {
        System.out.println("Target configs: " + targetConfigs.toString());
        this.targetConfigs = targetConfigs;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
