package flipkart.alert.dispatch;

import com.yammer.dropwizard.logging.Log;
import flipkart.alert.config.DispatcherConfiguration;
import flipkart.alert.domain.DispatcherContainer;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatchQueue {
    public static BlockingQueue<DispatcherContainer> statusQueue;
    private static Log log = Log.forClass(StatusDispatchQueue.class);

    public static void add(DispatcherContainer container) throws InterruptedException {
        statusQueue.offer(container, 300, TimeUnit.SECONDS);
        log.info("*****Adding " + container.getDispatcherObject() +"to queue. Size: " + statusQueue.size());
    }

    public static void add(List<DispatcherContainer> containers) throws InterruptedException {
        for(DispatcherContainer container : containers)
            add(container);
    }

    public static DispatcherContainer remove() throws InterruptedException {
        DispatcherContainer container =  statusQueue.take();
        log.info("*****Removing " + container.getDispatcherObject() + " to queue. Size: " + statusQueue.size());
        return  container;
    }

    public static void initiate(DispatcherConfiguration config) {
        statusQueue = new LinkedBlockingQueue<DispatcherContainer>(config.getQueueSize());
    }

    public static int getQueueSize(){
        return statusQueue.size();
    }

    public static BlockingQueue<DispatcherContainer> getQueue(){
        return statusQueue;
    }
}

