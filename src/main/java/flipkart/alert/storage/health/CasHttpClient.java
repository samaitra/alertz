package flipkart.alert.storage.health;

import com.flipkart.casclient.CASClientException;
import com.flipkart.casclient.client.HttpAuthClient;
import com.flipkart.casclient.entity.Request;
import com.flipkart.casclient.util.InMemoryCache;
import com.ning.http.client.Response;
import com.yammer.dropwizard.logging.Log;
import flipkart.alert.domain.Metric;
import flipkart.alert.storage.SourceClient;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 21/10/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */

@Setter @Getter
public class CasHttpClient extends SourceClient{

    private String casUrl;

    private String user;

    private String password;

    private Log log = Log.forClass(CasHttpClient.class);

    protected long startTime;

    protected long endTime;

    protected String loginUrl = "/login";

    protected String logoutUrl = "/logout";

    private static HttpAuthClient casClient;

    public CasHttpClient(Map<String,String> casInfo) {
        this.loginUrl = casInfo.get("casUrl") + loginUrl;
        this.logoutUrl = casInfo.get("casUrl") + logoutUrl;
        this.user = casInfo.get("user");
        this.password = casInfo.get("password");
        casClient = fetchClient();
    }

    @Override
    public List<Metric> execute() throws IOException, ParseException {
        List<Metric> metrics = new ArrayList<Metric>();
        Response response = null;
        try {
            startTimer();
            response = casClient.executeGet(new Request(getQuery(), null, null));
            stopTimer();

            metrics.add(new Metric(buildKeyFor("responseTime"), timeTaken()));
            metrics.add(new Metric(buildKeyFor("statusCode"), response.getStatusCode()));

        } catch (CASClientException e) {
            return null;
        } finally {
            if(response!=null) {
                logout();
            }
        }
        return metrics;
    }

    private void logout() {
        Request logoutRequest = new  Request(logoutUrl, null, null);
        casClient.executeGet(logoutRequest);
    }

    private HttpAuthClient fetchClient() {
        if(casClient == null) return new HttpAuthClient(loginUrl, user, password, true, new InMemoryCache());
        return casClient;
    }

    private void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    private void stopTimer() {
        this.endTime = System.currentTimeMillis();
    }

    private long timeTaken() {
        return endTime - startTime;
    }

    private String buildKeyFor(String metricName) {
        return metricName + "." + getQueryName();
    }
}
