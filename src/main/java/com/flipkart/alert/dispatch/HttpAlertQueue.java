package com.flipkart.alert.dispatch;

import com.flipkart.alert.domain.Alert;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 19/10/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class HttpAlertQueue {
    private static int maxQueueSize;
    private static String defaultQueueName;
    private static Map<String,BlockingQueue<Alert>> queues;

    static {
        queues = new HashMap<String, BlockingQueue<Alert>>();
        maxQueueSize = 10000;
        defaultQueueName = "defaultQueue";
    }

    public static void add(Alert alert) throws InterruptedException {
        add(alert, defaultQueueName);
    }

    public static  void add(Alert alert, String queueName) throws InterruptedException {
        add(Arrays.asList(new Alert[]{alert}), queueName);
    }

    public static void add(List<Alert> alerts, String queueName) throws InterruptedException {
        BlockingQueue<Alert> alertQueue = queues.get(queueName);
        if(alertQueue == null) {
            alertQueue = createQueue(queueName);
        }

        for(Alert alert : alerts)
            alertQueue.offer(alert, 300, TimeUnit.SECONDS);
        queues.put(queueName, alertQueue);
    }

    public static BlockingQueue<Alert> createQueue(String queueName) {
        BlockingQueue<Alert> alertQueue;
        alertQueue = new ArrayBlockingQueue<Alert>(maxQueueSize);
        queues.put(queueName, alertQueue);
        return alertQueue;
    }

    public static Alert remove(String queueName, boolean blocking) throws InterruptedException {
        Alert alert = null;
        List<Alert> alerts = remove(queueName, 1, blocking);
        if(alerts.size() > 0)
            alert = alerts.get(0);
        return alert;
    }

    public static List<Alert> remove(String queueName, int howMany, boolean blocking) throws InterruptedException {
        List<Alert> alerts = new ArrayList<Alert>();
        BlockingQueue<Alert> queue = queues.get(queueName);
        if(queue != null) {
            int waitingTime = 0;
            if(blocking)
                waitingTime = 300;

            for(int i=0; i<howMany; i++) {
                Alert alert = queue.poll(waitingTime, TimeUnit.SECONDS);
                if(alert == null)
                    break;
                alerts.add(alert);
            }
        }
        return alerts;
    }

    public static List<Alert> peek(String queueName, Integer howMany) {
        List<Alert> alerts = new ArrayList<Alert>();
        BlockingQueue<Alert> queue = queues.get(queueName);
        if(queue != null) {
           for(int i=0; i<howMany; i++) {
                Alert alert = queue.peek();
                if(alert == null)
                    break;
                alerts.add(alert);
            }
        }
        return alerts;
    }

    public static Alert peek(String queueName) {
        Alert alert = null;
        List<Alert> alerts = peek(queueName, 1);
        if(alerts.size() > 0)
            alert = alerts.get(0);
        return alert;
    }

    public static Integer queueSize(String queueName) throws InterruptedException {
        BlockingQueue<Alert> queue = queues.get(queueName);
        if(queue != null)
            return queue.size();
        return 0;
    }

    public static Map<String,Integer> queueSize() throws InterruptedException {
        Map<String,Integer> queueSizes = new HashMap<String, Integer>();
        for(String queueName : queues.keySet()) {
            queueSizes.put(queueName, queues.get(queueName).size());
        }
        return queueSizes;
    }

    synchronized public static void removeAllQueues() {
        queues.clear();
    }

}
