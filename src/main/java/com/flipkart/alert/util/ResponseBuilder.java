package com.flipkart.alert.util;

import javax.ws.rs.core.Response;

/**
 * Response Builder
 */
public class ResponseBuilder {
    public static Response response(Response.Status status, Object message) {
        return Response.status(status).entity(message).build();
    }

    public static Response badRequest(String message) {
        return ResponseBuilder.response(Response.Status.BAD_REQUEST, message);
    }

    public static Response badRequest(Exception e) {
        return ResponseBuilder.response(Response.Status.BAD_REQUEST, e);
    }

    public static Response notFound(String message) {
        return ResponseBuilder.response(Response.Status.NOT_FOUND, message);
    }

    public static Response duplicate(String message) {
        return ResponseBuilder.response(Response.Status.CONFLICT, message);
    }
}

