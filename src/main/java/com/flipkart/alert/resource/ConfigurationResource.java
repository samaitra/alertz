package com.flipkart.alert.resource;

import com.flipkart.alert.config.AlertServiceConfiguration;
import com.flipkart.alert.config.MetricArchiverConfiguration;
import com.flipkart.alert.util.ClassHelper;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



/**
 * User: nitinka
 * This endpoint can be used to check Application Configuration
 */

@Path("/configurations")
public class ConfigurationResource {
    private final AlertServiceConfiguration configuration;

    public ConfigurationResource(AlertServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public AlertServiceConfiguration getAlertServiceConfiguration() throws Exception {
        return configuration;
    }

    @Path("{confName}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public Response getConfiguration(@PathParam("confName") String confName) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        String tmpConfName = confName;
        for(int i=1;i<=2;i++) {
            try {
                Method method = ClassHelper.getMethod(configuration.getClass().getCanonicalName(), pruneMethodName(tmpConfName), new Class[]{});
                return Response.status(200).entity(method.invoke(configuration, new Object[]{})).build();
            } catch (NoSuchMethodException e) {
                if(i==1) {
                    tmpConfName += "Configuration";
                    continue;
                }
            }
        }
        return Response.status(404).entity("Configuration " + confName + " Not Found").build();
    }

    private String pruneMethodName(String confName) {
        return "get"+confName.substring(0,1).toUpperCase()+confName.substring(1);
    }
}
