package flipkart.alert.integration;

import flipkart.alert.config.AlertServiceConfiguration;
import flipkart.alert.dispatch.StatusDispatchPipeline;
import flipkart.alert.domain.MetricSource;
import flipkart.alert.resource.DataArchivalResource;
import flipkart.alert.resource.HttpAlertQueueResource;
import flipkart.alert.resource.ScheduledRuleResource;
import flipkart.alert.storage.MetricSourceClientFactory;
import flipkart.alert.storage.OpenTsdbClient;
import flipkart.alert.storage.RuleEventsFactory;
import flipkart.alert.testHelper.HttpAlertQueueClient;
import flipkart.alert.testHelper.ScheduledRuleResourceClient;
import flipkart.alert.testHelper.TestNGResourceTest;
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
        configuration = (AlertServiceConfiguration) yaml.load(new FileInputStream("config/fk-alert-service-test.yml"));

        metricSourceClientFactory = MetricSourceClientFactory.buildFactory(MetricSource.getAll(MetricSource.class),
                configuration.getMetricSourceClients());

        addResource(new HttpAlertQueueResource());
        addResource(new ScheduledRuleResource());
        addResource(new DataArchivalResource(configuration.getDataArchivalConfiguration()));

        RuleEventsFactory.buildFactory(configuration.getruleEventsConfiguration());

        StatusDispatchPipeline.buildPipeline(configuration.getDispatcherConfiguration());
        OpenTsdbClient.INSTANCE.initialize(configuration.getDataArchivalConfiguration());


    }

    @BeforeClass
    public void beforeClassIntegrationBase() {
        scheduledRuleClient = new ScheduledRuleResourceClient(client());
        httpAlertQueueClient = new HttpAlertQueueClient(client());
    }
}
