package com.flipkart.alert.storage;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/11/12
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */

public abstract class AbstractHttpSourceClient extends SourceClient {
    private String host;
    private int port;
    protected static AsyncHttpClient httpClient;

    static {
        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setAllowPoolingConnection(true).
                setMaximumConnectionsTotal(1000).
                setCompressionEnabled(true).
                setRequestTimeoutInMs(60000);
        httpClient = new AsyncHttpClient(builder.build());
    }
    public AbstractHttpSourceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public AbstractHttpSourceClient setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public AbstractHttpSourceClient setPort(int port) {
        this.port = port;
        return this;
    }
}
