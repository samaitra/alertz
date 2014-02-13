package com.flipkart.alert.storage.archiver;

import com.flipkart.alert.config.MetricArchiverConfiguration;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.util.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 14/1/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class MetricArchiver {

    protected Logger logger = LoggerFactory.getLogger(MetricArchiver.class);
    public static enum METRIC_TYPE {
        METRIC, BREACH
    }

    public static class MetricInstance {
        private long time;
        private double value;

        public MetricInstance(long time, double value) {
            this.time = time;
            this.value = value;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
    private Map<String, Object> params;
    protected MetricArchiver(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public static MetricArchiver build(MetricArchiverConfiguration configuration) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return (MetricArchiver) ClassHelper.createInstance(Class.forName(configuration.getMetricArchiverClass()),
                new Class[]{Map.class}, new Object[]{configuration.getParams()});
    }

    abstract public void archive(String ruleName, List<Metric> metrics) throws IOException;
    abstract public InputStream retrieve(String ruleName, List<String> metrics,METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException;
    abstract public Map<String, List<MetricInstance>> retrieveRaw(String ruleName, List<String> metrics, METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException;
}
