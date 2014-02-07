package flipkart.alert.config;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DispatcherConfiguration {
    private int queueSize;
    private HashMap<String, StatusConfig> targetConfigs;
    private int poolSize;

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public HashMap<String, StatusConfig> getTargetConfigs() {
        return targetConfigs;
    }

    public void setTargetConfigs(HashMap<String, StatusConfig> targetConfigs) {
        System.out.println("Target configs: " + targetConfigs.toString());
        this.targetConfigs = targetConfigs;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
