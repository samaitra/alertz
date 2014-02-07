package flipkart.alert.resource;

import com.yammer.dropwizard.jersey.params.BooleanParam;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.metrics.annotation.Timed;
import flipkart.alert.dispatch.HttpAlertQueue;
import flipkart.alert.domain.Alert;
import flipkart.alert.domain.Metric;
import flipkart.alert.domain.RuleCheck;
import org.quartz.SchedulerException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.*;

/**
 * User: nitinka
 */
@Path("/alerts")
@Produces(MediaType.APPLICATION_JSON)
public class HttpAlertQueueResource {
    @GET
    @Timed
    public Map<String,Integer> getAlerts() throws InterruptedException {
        return HttpAlertQueue.queueSize();
    }

    @Path(value = "/queues/{queueName}")
    @GET
    @Timed
    public List<Alert> getAlert(@PathParam("queueName") String queueName,
                                @QueryParam(value = "count") @DefaultValue(value = "1") IntParam count,
                                @QueryParam("removeAlert") @DefaultValue("false")BooleanParam removeAlert)
            throws ParseException, SchedulerException, InterruptedException {

        if(removeAlert.get())
            return HttpAlertQueue.remove(queueName,count.get(),false);
        return HttpAlertQueue.peek(queueName,count.get());
    }

    @Path(value = "/queues/{queueName}/formats/{format}")
    @GET
    @Timed
    public Map<String,String> getFormatedAlert(@PathParam("queueName") String queueName,
                                               @PathParam("format") String format,
                                               @QueryParam("removeAlert") @DefaultValue("false")BooleanParam removeAlert)
            throws ParseException, SchedulerException, InterruptedException {

        Alert alert = null;
        if(removeAlert.get())
            alert = HttpAlertQueue.remove(queueName,false);
        else
            alert = HttpAlertQueue.peek(queueName);

        Map<String,String> alertInfoMap = new HashMap<String, String>();

        if(alert!=null) {
            if(format.equalsIgnoreCase("NAGIOS")) {
                alertInNagiosFormat(alert, alertInfoMap);
            }
        }
        return alertInfoMap;
    }

    @Path(value = "/queues/{queueName}/meta")
    @GET
    @Timed
    public Integer getAlertQueueMeta(@PathParam("queueName") String queueName) throws InterruptedException {
        return HttpAlertQueue.queueSize(queueName);
    }

    private void alertInNagiosFormat(Alert alert, Map<String, String> alertInfoMap) {
        List<Metric> metrics = alert.getMetrics();
        String metricStr = "";

        for(Metric metric: metrics) {
            metricStr += metric.getKey() + "=" + metric.getValue() + ",";
        }
        if(metricStr.length()>0)
            metricStr = metricStr.substring(0, metricStr.length()-1);

        List <RuleCheck> breachedChecks = alert.getBreachedChecks();
        String description= "";
        List<String> alertLevels = new ArrayList<String>();
        for (RuleCheck breachedCheck : breachedChecks) {
            description  = description + breachedCheck.getDescription() + ' ';
            alertLevels.add(breachedCheck.getAlertLevel());
        }

        if(description == null || description.trim().equals("")) {
            for (RuleCheck breachedCheck : breachedChecks) {
                description  = description + breachedCheck.getBooleanExpression() + ' ';
            }
        }
        alertInfoMap.put("message",
                String.format(alert.getAlertDate().toString()
                        + ": Metrics (%s) breached check with description '%s'",
                        metricStr,
                        description));

        alertInfoMap.put("alertLevel", Collections.max(alertLevels));
    }
}
