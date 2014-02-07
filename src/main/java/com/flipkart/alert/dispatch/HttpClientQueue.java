package com.flipkart.alert.dispatch;

import com.flipkart.alert.storage.SourceClient;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 09/04/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientQueue {
    private static int maxQueueSize;
    private static String defaultQueueName;
    private static Map<String,BlockingQueue<SourceClient>> queues;

    static {
        queues = new HashMap<String, BlockingQueue<SourceClient>>();
        maxQueueSize = 50;
        defaultQueueName = "defaultQueue";
    }


    public static void add(SourceClient client, String queueName) throws InterruptedException{
        BlockingQueue<SourceClient> clientQueue = queues.get(queueName);
        if(clientQueue == null) {
            clientQueue = createQueue(queueName);
        }
        clientQueue.offer(client, 300, TimeUnit.SECONDS);
        queues.put(queueName, clientQueue);
    }

    private static BlockingQueue<SourceClient> createQueue(String queueName) {
        BlockingQueue<SourceClient> clientQueue;
        clientQueue = new ArrayBlockingQueue<SourceClient>(maxQueueSize);
        queues.put(queueName, clientQueue);
        return clientQueue;
    }

    public static SourceClient peek(String queueName) throws InterruptedException{
        BlockingQueue<SourceClient> queue = queues.get(queueName);
        return queue.peek();
    }

    public static SourceClient remove(String queueName) throws InterruptedException{
        BlockingQueue<SourceClient> queue = queues.get(queueName);
        return queue.poll(300, TimeUnit.SECONDS);
    }
}
