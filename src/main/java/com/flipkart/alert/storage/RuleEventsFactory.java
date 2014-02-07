package com.flipkart.alert.storage;

import com.flipkart.alert.config.RuleEventsConfiguration;
import com.flipkart.alert.domain.OnDemandRuleEvent;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 02/05/13
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RuleEventsFactory {
    private static ExecutorService executorService;
    private static BlockingQueue<OnDemandRuleEvent> eventsQueue;
    private static final Object queueLock = new Object();


    public static void buildFactory(RuleEventsConfiguration ruleEventsConfiguration) {
        executorService = Executors.newFixedThreadPool(ruleEventsConfiguration.getPoolSize());
        eventsQueue = new ArrayBlockingQueue<OnDemandRuleEvent>(ruleEventsConfiguration.getQueueSize());
    }

    public static void addEvents(List<OnDemandRuleEvent> events)  throws InterruptedException {
        synchronized (queueLock) {
            eventsQueue.addAll(events);
        }
        for(int i=0; i < events.size(); i++) {
            executorService.execute(new RuleEventsManager());
        }
    }

    public static BlockingQueue<OnDemandRuleEvent> getEventsQueue() {
        return eventsQueue;
    }

}

class RuleEventsManager implements Runnable{

    @Override
    public void run() {
        try {
            OnDemandRuleEvent event = RuleEventsFactory.getEventsQueue().take();
            System.out.println("Event is " + event.toString());
            event.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
