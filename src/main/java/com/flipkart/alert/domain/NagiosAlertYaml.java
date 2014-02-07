package com.flipkart.alert.domain;

import com.yammer.dropwizard.logging.Log;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 14/4/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class NagiosAlertYaml implements Serializable {

    public String Alias;
    public String Monitoring;

    public Map<String,NagiosServiceEscalation> ServiceEscalations;
    public Map<String,NagiosService> Services;

    private static Log log = Log.forClass(NagiosAlertYaml.class);

    public String toString() {
        return "Alias: " + Alias + " Monitoring:" + Monitoring + " Service Escalations: " + ServiceEscalations + "Services: " + Services;
    }
}
