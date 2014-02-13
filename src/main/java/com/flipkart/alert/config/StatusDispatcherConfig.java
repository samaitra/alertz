package com.flipkart.alert.config;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 17/04/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatcherConfig {
    private String className;
    private Map<String,Object> defaultParams;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, Object> getDefaultParams() {
        return defaultParams;
    }

    public void setDefaultParams(Map<String, Object> defaultParams) {
        this.defaultParams = defaultParams;
    }

    public void addParams(String param, Object value) {
        this.defaultParams.put(param, value);
    }

}
