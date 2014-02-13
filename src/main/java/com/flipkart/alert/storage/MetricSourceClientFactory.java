package com.flipkart.alert.storage;

import com.flipkart.alert.domain.MetricSource;
import com.flipkart.alert.util.ClassHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 6/12/12
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricSourceClientFactory {

    private static MetricSourceClientFactory factory;
    private Map<String,String> metricSourceClientClassMap;
    private Map<String, MetricSource> metricSourceMap;

    private MetricSourceClientFactory(List<MetricSource> metricSources, Map<String,String> metricSourceClientClassMap) {

        this.metricSourceClientClassMap = metricSourceClientClassMap;
        this.metricSourceMap = new HashMap<String, MetricSource>();

        for(MetricSource metricSource : metricSources) {
            String metricSourceName = metricSource.getName();
            metricSourceMap.put(metricSourceName, metricSource);
        }
    }

    public static MetricSourceClientFactory clientFactory() {
        return factory;
    }

    public static MetricSourceClientFactory buildFactory(List<MetricSource> metricSources,
                                                         Map<String, String> metricSourceClientClassMap)
            throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        factory = new MetricSourceClientFactory(metricSources, metricSourceClientClassMap);
        return factory;
    }

    public void addMetricSource(MetricSource metricSource)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String metricSourceName = metricSource.getName();
        metricSourceMap.put(metricSourceName, metricSource);
    }

    /**
     * Needed for unit integration tests. Used to pass stubbed http client
     * @param metricSourceName
     * @param client
     */
    public void setClient(String metricSourceName, Object client) throws InterruptedException{
        HttpClientQueue.add((AbstractHttpSourceClient)client, metricSourceName);
    }

    public SourceClient getClient(String metricSourceName) throws InterruptedException {
        return HttpClientQueue.remove(metricSourceName);
    }

    public void prepareClientForSource(String metricSourceName)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, InterruptedException {

        MetricSource metricSource = metricSourceMap.get(metricSourceName);
        SourceClient client = (SourceClient) ClassHelper.createInstance(Class.forName(metricSourceClientClassMap.get(metricSource.getSourceType())),
                new Class[]{Map.class},
                new Object[]{metricSource.paramsToMap()});
        HttpClientQueue.add(client, metricSourceName);
    }

    private static class HttpClientQueue {
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
}