package com.flipkart.alert.config;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 30/04/13
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class RuleEventsConfiguration {
    private int poolSize;
    private int queueSize;

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
}
