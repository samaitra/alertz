package com.flipkart.alert.dispatch;

import com.flipkart.alert.config.StatusDispatcherConfig;
import com.flipkart.alert.config.StatusDispatcherServiceConfiguration;
import com.flipkart.alert.domain.*;
import com.flipkart.alert.util.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 13/2/14
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatchService {
    private static StatusDispatcherServiceConfiguration configuration;
    private static LinkedBlockingQueue<DispatchContainer> dispatchQueue;
    private static Logger logger = LoggerFactory.getLogger(StatusDispatchService.class);
    private static boolean initialized;

    public static void initialize(StatusDispatcherServiceConfiguration configuration) {
        if(!initialized) {
            StatusDispatchService.configuration = configuration;
            dispatchQueue = new LinkedBlockingQueue<DispatchContainer>(configuration.getQueueSize());
            for(int i=1;i<=configuration.getPoolSize();i++) {
                new StatusDispatcherThread().start();
            }
            initialized = true;
        }
    }

    public static boolean dispatch(Rule rule, Alert alert) {
        return dispatchQueue.offer(new DispatchContainer(rule, alert));
    }

    private static class DispatchContainer {
        private final Rule rule;
        private final Alert alert;
        public DispatchContainer(Rule rule, Alert alert) {
            this.rule = rule;
            this.alert = alert;
        }
    }

    private static class StatusDispatcherThread implements Runnable{
        @Override
        public void run() {
            logger.info("Started");
            while(true) {
                try {
                    DispatchContainer dispatchContainer = dispatchQueue.take();
                    Rule rule = dispatchContainer.rule;
                    Alert alert = dispatchContainer.alert;

                    Set<EndPoint> endPoints = rule.getEndPoints();

                    for(EndPoint endPoint: endPoints) {
                        StatusDispatcherConfig dispatcherConfig = configuration.getTargetConfigs().get(endPoint.getType());

                        Map<String,Object> configurableParams = new HashMap<String, Object>();
                        configurableParams.putAll(dispatcherConfig.getDefaultParams());
                        for(EndPointParam param: endPoint.getEndPointParams()) {
                            configurableParams.put(param.getName(), param.getValue());
                        }

                        try {
                            StatusDispatcher statusDispatcher = ClassHelper.createInstance((Class<StatusDispatcher>)Class.forName(dispatcherConfig.getClassName()));
                            if(alert != null) {
                                statusDispatcher.dispatch(alert, configurableParams);
                            }
                            else if(endPoint.isPublishAlways()) {
                                statusDispatcher.dispatch(rule, configurableParams);
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InstantiationException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
                catch(Exception e) {
                    logger.error("", e);
                }
            }
        }

        public Thread start() {
            Thread t = new Thread(this);
            t.start();
            return t;
        }
    }
}
