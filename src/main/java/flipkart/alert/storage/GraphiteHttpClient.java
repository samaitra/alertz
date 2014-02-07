package flipkart.alert.storage;

import com.ning.http.client.Response;
import com.yammer.dropwizard.logging.Log;
import flipkart.alert.domain.Metric;
import flipkart.alert.util.DateHelper;
import flipkart.alert.util.MetricHelper;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/11/12
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */

public class GraphiteHttpClient extends AbstractHttpSourceClient{
    private static Log log = Log.forClass(GraphiteHttpClient.class);
    private static final String RESPONSE_FORMAT ="json";

    public GraphiteHttpClient(Map<String,String> connectionParam) {
        super(connectionParam.get("graphiteHost"),
                Integer.parseInt(connectionParam.get("graphitePort")));
    }

    public List<Metric> execute(){
        List<Metric> metrics = new ArrayList<Metric>();
        DescriptiveStatistics stats = new DescriptiveStatistics();

        String[] graphiteUrls = new String[]{
                "http://"+this.getHost()+ ":" +this.getPort()
                        + "/render?"
                        + target()
                        + "&format="+ RESPONSE_FORMAT,
                "http://"+this.getHost()+ ":" +this.getPort()
                        + "/render?"
                        + this.getQuery()
                        + "&format="+ RESPONSE_FORMAT
        };

        for(int i=0; i<graphiteUrls.length;i++) {
            if(i==1) {
                log.info("Trying Without Encoding the Query");
            }
            String graphiteUrl = graphiteUrls[i];
            try {

                Future<Response> futureRes = httpClient.prepareGet(graphiteUrl).
                        execute();
                Response response = futureRes.get();

                if(response.getStatusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();

                    String dataStr = response.getResponseBody();

                    List<LinkedHashMap<String,Object>> data = mapper.readValue(dataStr, List.class);

                    // Following piece of code would move to actual http client implementation of Graphite or any other source
                    if(data.size() > 0) {
                        List<List> timeValueSeries = (List<List>) data.get(0).get("datapoints");
                        log.info("Time value series returned: " + timeValueSeries);
                        for(List timeValue: timeValueSeries) {
                            if(timeValue.get(0) != null) {
                                Double value = Double.parseDouble(timeValue.get(0).toString());
                                if(value != null) {
                                      stats.addValue(value);
                                }
                            }
                        }
                        metrics.addAll(MetricHelper.fetchPredefinedStatistics(stats, this.getQueryName()));
                    }
                    else {
                        log.warn("0 Data Points observed with URL "+graphiteUrl);

                    }
                }

            }catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        log.info("Computed Metrics :"+metrics.toString());
        return metrics;

    }

    private String target() {
        return (URLDecoder.decode(this.getQuery()).equals(this.getQuery()) ? java.net.URLEncoder.encode(this.getQuery()) : this.getQuery());
    }
}
