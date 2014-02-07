package com.flipkart.alert.testHelper;

import com.sun.jersey.api.client.Client;
import com.flipkart.alert.domain.Alert;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 17/12/12
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpAlertQueueClient {

    private Client client;

    public HttpAlertQueueClient(Client client) {
        this.client = client;
    }

    public Map getAllAlerts() {
        return client.resource("/alerts").get(Map.class);
    }

    public Integer getAlertQueueMeta(String queueName) {
        return client.resource("/alerts/queues/"+queueName+"/meta").get(Integer.class);
    }

    public List<Alert> getAlerts(String queueName) {
        return CollectionHelper.
                transformList(client.
                        resource("/alerts/queues/"+queueName).
                        get(List.class),
                        Alert.class);
    }

    public List<Alert> getAlerts(String queueName, int count) {
        return CollectionHelper.
                transformList(client.
                        resource("/alerts/queues/"+queueName+"?count="+count).
                        get(List.class),
                        Alert.class);
    }

    public Map getNagiosFormattedAlert(String queueName) {
        return client.resource("/alerts/queues/"+queueName+"/formats/NAGIOS").get(Map.class);
    }

}
