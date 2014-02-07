package com.flipkart.alert.storage.archiver;

import com.flipkart.alert.config.MetricArchiverConfiguration;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.util.ClassHelper;

import java.lang.reflect.InvocationTargetException;
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

    abstract public void archive(String ruleName, List<Metric> metrics);

    abstract public String retrieveImg(String ruleName, String metricName, Map<String, Object> tags);
}
