package flipkart.alert.resource;

import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * nitinka
 */
@Path("/metricSourceTypes")
public class MetricSourceTypeResource {

    private Set<String> metricSourceTypes;

    public MetricSourceTypeResource(Set<String> metricSourceTypes) {
        this.metricSourceTypes = metricSourceTypes;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public Set<String> getAllMetricSources() throws Exception {
        return metricSourceTypes;
    }
}
