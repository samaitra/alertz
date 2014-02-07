package com.flipkart.alert.storage;

import com.ning.http.client.Response;
import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.util.JsonHelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * User: nitinka
 * Do Http Get, convert the complete json into flat absolute Map of key values
 */
public class HttpToFlatJsonClient extends AbstractHttpSourceClient{

    private static Log log = Log.forClass(HttpToFlatJsonClient.class);

    public HttpToFlatJsonClient(Map<String, String> connectionParam) {
        super(connectionParam.get("host"),
                Integer.parseInt(connectionParam.get("port")));
    }

    @Override
    public List<Metric> execute() throws IOException, ParseException {

        List<Metric> metrics = new ArrayList<Metric>();
        String requestUrl = "http://"+this.getHost()+":"+ this.getPort()
                + "/" + relativeURI();

        try {

            Future<Response> futureRes = httpClient.prepareGet(requestUrl).
                    execute();
            Response response = futureRes.get();

            if(response.getStatusCode() == 200) {

                String dataStr = response.getResponseBody();
                if(dataStr.equals(""))
                    return metrics;

                Map<String, Object> flattenedJson = JsonHelper.flatten(dataStr);
                for(String key : flattenedJson.keySet()) {
                    metrics.add(new Metric(key, flattenedJson.get(key)));
                }
            }
        }catch (NoSuchElementException e){
            log.warn("0 Metrics/Datapoints observed with URL " + requestUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return metrics;
    }

    private String relativeURI() throws ParseException {
        String relativeURI = this.getQuery();
        relativeURI = relativeURI.replace("{", "%7B");
        relativeURI = relativeURI.replace("}", "%7D");
        return relativeURI;
    }
}
