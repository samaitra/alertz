package com.flipkart.alert.helper;

import com.flipkart.alert.util.JsonHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.testng.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 18/12/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestJsonHelper {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(TestMetricHelper.class);

    @DataProvider(name="files")
    public Object[][] inputFiles() {
        return new String[][]{
                {"fixtures/testFlattenJsonWithFlatJson.json"},
                {"fixtures/testFlattenJsonWithNestedArrayJson.json"},
                {"fixtures/testFlattenJsonWithNestedHashJson.json"},
                {"fixtures/testFlattenJsonWithStartArray.json"}};
    }

    @Test(groups = {"smoke", "regression", "helper"}, dataProvider = "files")
    public void testFlattenedJson(String inputFile) throws ParseException, IOException {
        logger.info("Running Test with input file "+inputFile);
        Map<String, Object> testData =  mapper.readValue(jsonFixture(inputFile), Map.class);
        String inputJson = mapper.writeValueAsString(testData.get("json"));
        Map<String, Object> expectedFlattenedJsonMap = mapper.readValue(mapper.writeValueAsBytes(testData.get("flatJsonMap")), Map.class);
        Map<String, Object> observedFlattenedJsonMap = JsonHelper.flatten(inputJson);
        logger.info("Expected Flattened Json :"+mapper.defaultPrettyPrintingWriter().writeValueAsString(expectedFlattenedJsonMap));
        logger.info("Observed Flattened Json :"+mapper.defaultPrettyPrintingWriter().writeValueAsString(observedFlattenedJsonMap));
        assertEquals(observedFlattenedJsonMap, expectedFlattenedJsonMap);
    }
}
