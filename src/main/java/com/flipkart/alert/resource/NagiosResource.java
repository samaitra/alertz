package com.flipkart.alert.resource;

import com.flipkart.alert.domain.NagiosAlertYaml;
import com.flipkart.alert.domain.NagiosService;
import com.flipkart.alert.domain.NagiosServiceEscalation;
import com.flipkart.alert.storage.NagiosYamlWriter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 9/5/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/nagios")
public class NagiosResource {

    /*
    ** eg:
       {
            500Count: {
                        check_freshness: '0'
                        contact_groups: alert-service-test-group
                        host_name: all
                        normal_check_interval: 5
                        retry_check_interval: 5
                        use: w3_alert_service
                        },
            Login: {
                        check_freshness: '0'
                        contact_groups: alert-service-test-group
                        host_name: all
                        normal_check_interval: 5
                        retry_check_interval: 5
                        use: w3_alert_service
                        }
       }
     */
    @Path("/serviceEscalation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NagiosAlertYaml addServiceEscalation(Map<String,NagiosServiceEscalation> serviceEscalations) {
        NagiosYamlWriter.INSTANCE.addServiceEscalations(serviceEscalations);
        return NagiosYamlWriter.INSTANCE.getNagiosYaml();
    }

    @Path("/serviceEscalation")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NagiosAlertYaml removeServiceEscalation(List<String> escalationNames){
        NagiosYamlWriter.INSTANCE.removeServiceEscalations(escalationNames);
        return NagiosYamlWriter.INSTANCE.getNagiosYaml();
    }

    @Path("/service")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NagiosAlertYaml addService(Map<String, NagiosService> services) {
        NagiosYamlWriter.INSTANCE.addServices(services);
        return NagiosYamlWriter.INSTANCE.getNagiosYaml();
    }

    @Path("/service")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NagiosAlertYaml removeService(List<String> serviceNames) {
        NagiosYamlWriter.INSTANCE.removeService(serviceNames);
        return NagiosYamlWriter.INSTANCE.getNagiosYaml();
    }
}
