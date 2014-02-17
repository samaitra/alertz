package com.flipkart.alert;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 25/10/12
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */

import com.flipkart.alert.dispatch.StatusDispatchService;
import com.flipkart.alert.storage.archiver.MetricArchiverService;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import com.flipkart.alert.config.AlertServiceConfiguration;
import com.flipkart.alert.domain.MetricSource;
import com.flipkart.alert.health.DBHealthCheck;
import com.flipkart.alert.resource.*;
import com.flipkart.alert.storage.MetricSourceClientFactory;
import com.flipkart.alert.storage.RuleEventsFactory;

import java.lang.reflect.InvocationTargetException;

public class AlertService extends Service<AlertServiceConfiguration> {
    private static AlertServiceConfiguration configuration;

    private AlertService() {
        super("Alertz");
        addBundle(new AssetsBundle("/assets", "/"));
    }

    @Override
    protected void initialize(AlertServiceConfiguration configuration,
                              Environment environment) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        environment.addResource(new ScheduledRuleResource());
        environment.addResource(new HttpAlertQueueResource());
        environment.addResource(new MetricSourceResource());
        environment.addResource(new TeamResource());
        environment.addResource(new OnDemandRuleResource());
        environment.addResource(new MetricSourceTypeResource(configuration.getMetricSourceClients().keySet()));
        environment.addResource(new ConfigurationResource(configuration));
        environment.addResource(new MetricArchiverResource());
        environment.addHealthCheck(new DBHealthCheck("DatabaseHealthCheck"));


        MetricSourceClientFactory.buildFactory(MetricSource.getAll(MetricSource.class),
                configuration.getMetricSourceClients());

        RuleEventsFactory.buildFactory(configuration.getRuleEventsConfiguration());

        StatusDispatchService.initialize(configuration.getStatusDispatcherServiceConfiguration());
        MetricArchiverService.initialize(configuration.getMetricArchiverConfiguration());

//        GraphiteConfiguration graphiteConfig = configuration.getGraphiteConfiguration();
//        GraphiteReporter.enable(1, TimeUnit.SECONDS, graphiteConfig.getHost(), graphiteConfig.getPort(), graphiteConfig.getPrefix());
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new AlertService().run(args);
    }
}
