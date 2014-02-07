package flipkart.alert.dispatch;

import com.yammer.metrics.annotation.Timed;
import flipkart.alert.config.DispatcherConfiguration;
import flipkart.alert.config.StatusConfig;
import flipkart.alert.domain.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static flipkart.alert.util.ClassHelper.createInstance;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatchPipeline {
    private static HashMap<String, StatusConfig> targets;
    private static ExecutorService executorService = null;

    public static void buildPipeline(DispatcherConfiguration configuration) {
        targets = configuration.getTargetConfigs();
        executorService = Executors.newFixedThreadPool(configuration.getPoolSize());
        StatusDispatchQueue.initiate(configuration);
    }

    @Timed
    public static void publishToQueue(Object o, Set<EndPoint> endPoints) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
        for(EndPoint endPoint: endPoints) {
            Map<String,Object> configurableParams = new HashMap<String, Object>();
            StatusConfig configForEndPoint = targets.get(endPoint.getType());

            configurableParams.putAll(configForEndPoint.getDefaultParams());

            if(endPoint.getEndPointParams().size() > 0) {
                for(EndPointParam param: endPoint.getEndPointParams()) {
                    configurableParams.put(param.getName(), param.getValue());
                }
            }

            String dispatcherClassName = targets.get(endPoint.getType()).getClassName();
            StatusDispatcher statusDispatcher = createInstance((Class<StatusDispatcher>)Class.forName(dispatcherClassName));

            DispatcherContainer container = new DispatcherContainer(o, statusDispatcher, configurableParams, endPoint.isPublishAlways());

            StatusDispatchQueue.add(container);
            executorService.execute(new StatusDispatcherThread());
        }

    }
}
