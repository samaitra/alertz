package com.flipkart.alert.resource;

import com.yammer.metrics.annotation.Timed;
import com.flipkart.alert.config.DataArchivalConfiguration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 21/10/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/dataArchivalSource")
public class DataArchivalResource {
    private final DataArchivalConfiguration archivalConfiguration;

    public DataArchivalResource(DataArchivalConfiguration archivalConfiguration) {
        this.archivalConfiguration = archivalConfiguration;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public DataArchivalConfiguration getConfiguration() throws Exception {
        return this.archivalConfiguration;
    }
}
