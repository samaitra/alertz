package com.flipkart.alert.integration;

import com.flipkart.alert.config.AlertServiceConfiguration;
import com.flipkart.alert.dispatch.StatusDispatchService;
import com.flipkart.alert.domain.MetricSource;
import com.flipkart.alert.resource.HttpAlertQueueResource;
import com.flipkart.alert.resource.ScheduledRuleResource;
import com.flipkart.alert.storage.MetricSourceClientFactory;
import com.flipkart.alert.storage.RuleEventsFactory;
import com.flipkart.alert.storage.archiver.MetricArchiverService;
import com.flipkart.alert.testHelper.HttpAlertQueueClient;
import com.flipkart.alert.testHelper.ScheduledRuleResourceClient;
import com.flipkart.alert.testHelper.TestNGResourceTest;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 11/12/12
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseIntegrationTest extends TestNGResourceTest {
    protected ObjectMapper mapper = new ObjectMapper();
    protected AlertServiceConfiguration configuration;
    protected MetricSourceClientFactory metricSourceClientFactory;
    protected ScheduledRuleResourceClient scheduledRuleClient;
    protected HttpAlertQueueClient httpAlertQueueClient;

    @Override
    protected void setUpResources() throws Exception {
        initiateService();
    }

    private void initiateService() throws FileNotFoundException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Loader loader = new Loader(new Constructor(AlertServiceConfiguration.class));
        Yaml yaml = new Yaml(loader);
        configuration = (AlertServiceConfiguration) yaml.load(new FileInputStream("config/alertz-test.yml"));

        metricSourceClientFactory = MetricSourceClientFactory.buildFactory(MetricSource.getAll(MetricSource.class),
                configuration.getMetricSourceClients());

        addResource(new HttpAlertQueueResource());
        addResource(new ScheduledRuleResource());

        RuleEventsFactory.buildFactory(configuration.getruleEventsConfiguration());

        StatusDispatchService.initialize(configuration.getStatusDispatcherServiceConfiguration());
        MetricArchiverService.initialize(configuration.getMetricArchiverConfiguration());
    }

    @BeforeClass
    public void beforeClassIntegrationBase() {
        scheduledRuleClient = new ScheduledRuleResourceClient(client());
        httpAlertQueueClient = new HttpAlertQueueClient(client());
    }
}
