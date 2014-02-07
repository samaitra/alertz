package com.flipkart.alert.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 18/12/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonHelper {
    private static ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> flatten(String jsonString) throws IOException {
        Object jsonObj = mapper.readValue(jsonString, Object.class);
        return flattenTheJsonMap(jsonObj,null);
    }

    private static Map<String, Object> flattenTheJsonMap(Object jsonNode, String baseKey) throws IOException {
        LinkedHashMap<String, Object> flattenedJsonMap = new LinkedHashMap<String, Object>();

        if(jsonNode instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> jsonMap = (LinkedHashMap<String, Object>) jsonNode;
            for(String key : jsonMap.keySet()) {
                Object value = jsonMap.get(key);
                String absoluteKey = baseKey == null ? key : baseKey + "." + key;
                if(value instanceof LinkedHashMap || value instanceof ArrayList) {
                    flattenedJsonMap.putAll(flattenTheJsonMap(value, absoluteKey));
                }
                else {
                    flattenedJsonMap.put(absoluteKey, value);
                }
            }
        }
        else if(jsonNode instanceof ArrayList) {
            ArrayList<Object> listOfValues = (ArrayList<Object>) jsonNode;
            for(int index=0; index<listOfValues.size();index++) {
                Object indexValue = listOfValues.get(index);
                String absoluteKey = baseKey == null ? "["+index+"]" : baseKey + "["+index+"]";

                if(indexValue instanceof LinkedHashMap || indexValue instanceof ArrayList) {
                    flattenedJsonMap.putAll(flattenTheJsonMap(indexValue, absoluteKey));
                }
                else {
                    flattenedJsonMap.put(absoluteKey, indexValue);
                }
            }
        }

        return flattenedJsonMap;
    }
}
