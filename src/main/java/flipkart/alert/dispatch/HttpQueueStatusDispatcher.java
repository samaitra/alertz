package flipkart.alert.dispatch;

import com.yammer.dropwizard.logging.Log;
import flipkart.alert.domain.Alert;
import flipkart.alert.domain.Rule;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class HttpQueueStatusDispatcher implements StatusDispatcher {
    private static Log log = Log.forClass(HttpQueueStatusDispatcher.class);

    @Override
    public void dispatch(Alert alert, Map<String, Object> defaultParams) {
        String defaultQueue = alert.getRule().getName() + "-" + alert.getRule().getTeam();
        try {
            if(defaultParams.keySet().contains("queueName"))
                HttpAlertQueue.add(alert, defaultParams.get("queueName").toString());
            else
                HttpAlertQueue.add(alert, defaultQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatch(Rule rule, Map<String, Object> defaultParams) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
