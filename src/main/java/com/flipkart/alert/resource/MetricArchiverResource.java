package com.flipkart.alert.resource;

/**
 * User: NitinK.Agarwal@yahoo.com
 */

import com.flipkart.alert.domain.Rule;
import com.flipkart.alert.domain.ScheduledRule;
import com.flipkart.alert.storage.archiver.MetricArchiver;
import com.flipkart.alert.storage.archiver.MetricArchiverService;
import com.flipkart.alert.util.DateHelper;
import com.flipkart.alert.util.NumberHelper;
import com.flipkart.alert.util.ResponseBuilder;
import com.yammer.metrics.annotation.Timed;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Path("/archivedMetrics")
public class MetricArchiverResource {

    private static ObjectMapper mapper = new ObjectMapper();
    private static String DATE_FORMAT = "yyyy/MM/dd-HH:mm:ss";

    @Path("rules/{ruleId}")
    @GET
    @Timed
    public Response getMetrics(@PathParam("ruleId") String ruleId,
                                               @QueryParam("start") String startTime,
                                               @QueryParam("end") String endTime,
                                               @QueryParam("metricType") @DefaultValue("METRIC") String metricTypeStr,
                                               @QueryParam("format") @DefaultValue("img") String format)
            throws IOException, ExecutionException, InterruptedException, ParseException {
        Rule rule = searchRule(ruleId);
        MetricArchiver.METRIC_TYPE metricType = MetricArchiver.METRIC_TYPE.valueOf(metricTypeStr);
        return getResponse(startTime, endTime, format, rule, metricType);
    }

    private Response getResponse(String startTime, String endTime, String format, Rule rule, MetricArchiver.METRIC_TYPE metricType) throws IOException, ParseException {
        if("img".equals(format)) {
            return Response.status(200).
                    type(MediaType.APPLICATION_OCTET_STREAM).
                    entity(MetricArchiverService.retrieve(rule.getName(),
                            getMetrics(rule, metricType),
                            metricType,
                            DateHelper.parseDate(startTime,DATE_FORMAT),
                            DateHelper.parseDate(endTime,DATE_FORMAT))).
                    build();
        } else {
            return Response.status(200).
                    type(MediaType.APPLICATION_JSON).
                    entity(MetricArchiverService.retrieveRaw(rule.getName(),
                            getMetrics(rule, metricType),
                            metricType,
                            DateHelper.parseDate(startTime,DATE_FORMAT),
                            DateHelper.parseDate(endTime,DATE_FORMAT))).
                    build();
        }
    }

    private List<String> getMetrics(Rule rule, MetricArchiver.METRIC_TYPE metricType) throws IOException {
        List<String> metrics = null;

        switch(metricType) {
            case BREACH:
                metrics = Arrays.asList(new String[]{rule.getName() + "." + "breached"});
                break;

            case METRIC:
                metrics = mapper.readValue(rule.getRuleMetrics(), List.class);
                metrics.remove(rule.getName() + "." + "breached");
                break;
        }
        return metrics;
    }

    private Rule searchRule(String ruleId) {
        if(NumberHelper.isNumber(ruleId)) {
            return ScheduledRule.getById(Rule.class, Long.parseLong(ruleId));
        }
        List<Rule> rules = Rule.getByColumn(Rule.class,"name",ruleId);
        Rule searchedRule = rules.size() > 0 ? rules.get(0) : null;

        if(searchedRule != null) {
            return searchedRule;
        }
        throw new WebApplicationException(ResponseBuilder.notFound("Rule With id " + ruleId + " not found"));
    }

}