package flipkart.alert.resource;

import com.yammer.metrics.annotation.Timed;
import flipkart.alert.domain.MetricSource;
import flipkart.alert.storage.MetricSourceClientFactory;
import flipkart.alert.util.NumberHelper;
import flipkart.alert.util.ResponseBuilder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * nitinka
 */
@Path("/metricSources")
public class MetricSourceResource {
    /*
    {
        "name": "ops.graphite",
        "sourceType": "GRAPHITE",
        "sourceConnectionParams": [
            {
                "param":"graphiteHost",
                "value": "ops-statsd.nm.flipkart.com"
            },
            {
                "param":"graphitePort",
                "value": "80"
            }
        ]
    }

    {
       "name": "scm.graphite",
       "sourceType": "GRAPHITE",
       "sourceConnectionParams":
       [
           {
               "param": "graphiteHost",
               "value": "flo-infra-3.nm.flipkart.com"
           },
           {
               "param": "graphitePort",
               "value": "35557"
           }
       ]
   }
    {
        "name": "w3.graphite",
        "sourceType": "GRAPHITE",
        "sourceConnectionParams": [
            {
                "param":"graphiteHost",
                "value": "w3-api3.nm.flipkart.com"
            },
            {
                "param":"graphitePort",
                "value": "80"
            }
        ]
    }

     */

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Timed
    public MetricSource createMetricSource(@Valid MetricSource metricSource) throws Exception {
        metricSource.create();
        MetricSourceClientFactory.clientFactory().addMetricSource(metricSource);
        return metricSource;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    @Path("/{sourceId}")
    public MetricSource getMetricSource(@PathParam("sourceId") String sourceId) throws Exception {
        return searchMetricSource(sourceId);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<MetricSource> getAllMetricSources() throws Exception {
        return MetricSource.getAll(MetricSource.class);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{sourceId}")
    @Timed
    public MetricSource updateMetricSource(@PathParam("sourceId") String sourceId,@Valid MetricSource withMetricSource) throws Exception {
        MetricSource searchedSource = searchMetricSource(sourceId);
        searchedSource.update(withMetricSource);
        MetricSourceClientFactory.clientFactory().addMetricSource(withMetricSource);
        return withMetricSource;
    }

    @DELETE
    @Timed
    @Path("/{sourceId}")
    public void deleteMetricSource(@PathParam("sourceId") String sourceId) throws Exception {
        searchMetricSource(sourceId).delete();
    }

    private MetricSource searchMetricSource(String sourceId) {
        if(NumberHelper.isNumber(sourceId)) {
            return MetricSource.getById(MetricSource.class, Integer.parseInt(sourceId));
        }
        List<MetricSource> sources = MetricSource.getByColumn(MetricSource.class,"name",sourceId);
        MetricSource source =  sources.size() > 0 ? sources.get(0) : null;

        if(source != null)
            return source;
        throw new WebApplicationException(ResponseBuilder.notFound("Metric Source With id " + sourceId + " not found"));
    }
}
