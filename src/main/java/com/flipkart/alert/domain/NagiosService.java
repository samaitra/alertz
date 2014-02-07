package com.flipkart.alert.domain;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 9/5/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class NagiosService {
    private char check_freshness;
    private String host_name = "all";
    private int normal_check_interval;
    private int retry_check_interval;
    private String use = "w3-alert-service";
    private String contact_groups;

    @Override
    public String toString() {
        return "NagiosService{" +
                "check_freshness=" + check_freshness +
                ", host_name='" + host_name + '\'' +
                ", normal_check_interval=" + normal_check_interval +
                ", retry_check_interval=" + retry_check_interval +
                ", use='" + use + '\'' +
                ", contact_groups=" + contact_groups +
                '}';
    }

    public char getCheck_freshness() {
        return check_freshness;
    }

    public void setCheck_freshness(char check_freshness) {
        this.check_freshness = check_freshness;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public int getNormal_check_interval() {
        return normal_check_interval;
    }

    public void setNormal_check_interval(int normal_check_interval) {
        this.normal_check_interval = normal_check_interval;
    }

    public int getRetry_check_interval() {
        return retry_check_interval;
    }

    public void setRetry_check_interval(int retry_check_interval) {
        this.retry_check_interval = retry_check_interval;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getContact_groups() {
        return contact_groups;
    }

    public void setContact_groups(String contact_groups) {
        this.contact_groups = contact_groups;
    }
}
