package com.flipkart.alert.storage.archiver;

import com.flipkart.alert.config.MetricArchiverConfiguration;
import com.flipkart.alert.domain.Metric;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 12/2/14
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricArchiverService {
    private static MetricArchiver metricArchiver;
    private static MetricArchiverConfiguration configuration;

    public static void initialize(MetricArchiverConfiguration configuration)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(metricArchiver == null) {
            MetricArchiverService.configuration = configuration;
            metricArchiver = MetricArchiver.build(configuration);
        }
    }

    public static void archive(String ruleName, List<Metric> metrics) throws IOException {
        if(configuration.isEnabled()) {
            metricArchiver.archive(ruleName, metrics);
        }
    }

    public static InputStream retrieve(String ruleName, List<String> metrics, MetricArchiver.METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException {
        return metricArchiver.retrieve(ruleName, metrics, metric_type, startTime, endTime);
    }

    public static Map<String, List<MetricArchiver.MetricInstance>> retrieveRaw(String ruleName, List<String> metrics, MetricArchiver.METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException {
        return metricArchiver.retrieveRaw(ruleName, metrics, metric_type, startTime, endTime);
    }
}
