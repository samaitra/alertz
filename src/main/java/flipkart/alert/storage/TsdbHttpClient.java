package flipkart.alert.storage;

import com.ning.http.client.Response;
import com.yammer.dropwizard.logging.Log;
import flipkart.alert.domain.Metric;
import flipkart.alert.util.MetricHelper;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 13/05/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TsdbHttpClient extends AbstractHttpSourceClient{

    private static Log log = Log.forClass(TsdbHttpClient.class);
    private static final String RESPONSE_FORMAT ="ascii";

    public TsdbHttpClient(Map<String,String> connectionParam) {
        super(connectionParam.get("tsdbHost"),
                Integer.parseInt(connectionParam.get("tsdbPort")));
    }

    @Override
    public List<Metric> execute() throws IOException, ParseException {

        Map<String, DescriptiveStatistics> allHostStats = new LinkedHashMap<String, DescriptiveStatistics>();
        DescriptiveStatistics commonStats = new DescriptiveStatistics();

        List<Metric> metrics = new ArrayList<Metric>();
        String requestUrl = "http://"+this.getHost()+":"+ this.getPort()
                + "/q?" + relativeURI() + "&" + RESPONSE_FORMAT;

        try {

            Future<Response> futureRes = httpClient.prepareGet(requestUrl).
                    execute();
            Response response = futureRes.get();

            if(response.getStatusCode() == 200) {

                String dataStr = response.getResponseBody();
                if(dataStr.equals(""))
                    return metrics;
                StringTokenizer lineInfo = new StringTokenizer(dataStr, "\n");
                while(lineInfo.hasMoreElements()) {
                    String line = lineInfo.nextElement().toString();
                    if(line.contains(" ")) {
                        String[] data = line.split(" ");
                        Double value = Double.parseDouble(data[2]);

                        String host = null;
                        for(int tokenIndex=3;tokenIndex<data.length;tokenIndex++) {
                            if(data[tokenIndex].toUpperCase().startsWith("HOST")) {
                                host = data[tokenIndex].split("=")[1].trim();
                            }
                        }
                        if(host != null && !host.equals("")) {
                            DescriptiveStatistics hostStats = allHostStats.get(host);
                            if(hostStats == null) {
                                hostStats = new DescriptiveStatistics();
                                allHostStats.put(host, hostStats);
                            }
                            hostStats.addValue(value);
                        }

                        commonStats.addValue(value);
                    }
                }

                for(String host : allHostStats.keySet()) {
                    metrics.addAll(MetricHelper.fetchPredefinedStatistics(host+".",allHostStats.get(host), this.getQueryName()));
                }
                metrics.addAll(MetricHelper.fetchPredefinedStatistics(commonStats, this.getQueryName()));
                log.info("Computed Metrics :"+metrics.toString());
            }
        }catch (NoSuchElementException e){
            log.warn("0 Data Points observed with URL " + requestUrl);
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


//    public static void main(String[] args) throws ParseException, IOException {
//        Map<String,String> connectionParams = new HashMap<String, String>();
//        connectionParams.put("tsdbHost","w3-tsdb6.nm.flipkart.com");
//        connectionParams.put("tsdbPort","4242");
//
//        TsdbHttpClient client = new TsdbHttpClient(connectionParams);
//        List<Metric> metrics = client.
//                setQuery("start=2h-ago&m=max:CustomerService{stat=p_95,host=w3-console4,profiler=ConsoleController}").setQueryName("CustomerService").
//                setResponseFormat("ascii").
//                execute();
//    }


}
