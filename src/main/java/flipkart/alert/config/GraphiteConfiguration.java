package flipkart.alert.config;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 05/11/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphiteConfiguration {

    private String host;

    private int port;

    private String prefix;

    private String env;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
