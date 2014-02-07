package com.flipkart.alert.util;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 14/05/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryHelper {

    public static HashMap<String, String> getHashFromQuery(String query) {
        StringTokenizer params = new StringTokenizer(query, "&");
        HashMap<String, String> map = new HashMap<String, String>();

        while (params.hasMoreElements()) {
            String attribute = params.nextElement().toString();
            if(attribute.contains("=")) {
                String name = attribute.split("=")[0];
                String value = attribute.split("=", 2)[1];
                map.put(name, value);
            }
        }
        return map;
    }

    public static String getQueryFromHash(HashMap<String, String>queryMap) {
        return Joiner.on("&").withKeyValueSeparator("=").join(queryMap);
    }
}
