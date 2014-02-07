package com.flipkart.alert.helper;

import com.flipkart.alert.domain.AlertVariable;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.testHelper.CollectionHelper;
import com.flipkart.alert.util.MetricHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 10/12/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestMetricHelper {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TestMetricHelper.class);

    @DataProvider(name="files")
    public Object[][] inputFiles() {
        return new String[][]{
                {"fixtures/testResolveVariablesWithRegEx.json"},
                {"fixtures/testResolveVariablesWithNoRegEx.json"},
                {"fixtures/testResolveVariablesWithRegExAndRegularMetric.json"},
                {"fixtures/testResolveVariablesWithStringMetric.json"}};
    }

    @Test(groups = {"smoke", "regression", "helper"}, dataProvider = "files")
    public void testResolveVariables(String inputFile) throws ParseException, IOException {
        logger.info("Running Test with input file "+inputFile);
        Map<String, Object> testData =  mapper.readValue(jsonFixture(inputFile), Map.class);
        List<Metric> metrics = CollectionHelper.transformList(mapper.readValue(mapper.writeValueAsBytes(testData.get("metrics")),List.class), Metric.class);
        Set<AlertVariable> alertVariables = new HashSet<AlertVariable>(CollectionHelper.transformList(mapper.readValue(mapper.writeValueAsBytes(testData.get("alertVariables")),List.class), AlertVariable.class));
        List<Metric> expectedResolvedMetrics = CollectionHelper.transformList(mapper.readValue(mapper.writeValueAsBytes(testData.get("expectedResolvedMetrics")),List.class), Metric.class);
        logger.info(mapper.writeValueAsString(expectedResolvedMetrics));
        logger.info(mapper.writeValueAsString(MetricHelper.resolveVariablesAndCreateMetrics(alertVariables, metrics)));

        assertEquals(MetricHelper.resolveVariablesAndCreateMetrics(alertVariables, metrics), expectedResolvedMetrics);
    }
}
