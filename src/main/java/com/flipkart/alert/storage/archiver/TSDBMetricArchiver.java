package com.flipkart.alert.storage.archiver;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.config.DataArchivalConfiguration;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.storage.ChannelUpstreamHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 10/5/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TSDBMetricArchiver extends MetricArchiver{

    private static Log logger = Log.forClass(TSDBMetricArchiver.class);
    private ClientBootstrap nettyClient = null;

    private List <String> hosts;
    private final Integer defaultPort = 4242;
    private BlockingQueue < String > queue = new ArrayBlockingQueue<String>(10000);
    private ChannelFuture[] channelFutures;
    private final Object queueMutex = new Object();
    private DataArchivalConfiguration dataArchival;

    protected TSDBMetricArchiver(Map<String, Object> params) throws Exception {
        super(params);
        hosts = (List<String>) params.get("hosts");
        nettyClient = configureNettyServer();

        channelFutures = establishConnections(nettyClient,10, hosts, defaultPort);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Opentsdb-writer");
            }
        });
        // Get a handle, starting now, with a 10 second delay
        scheduler.scheduleAtFixedRate(new OpenTSDBWriter(), 0L, 10L, TimeUnit.SECONDS);
    }

    @Override
    public void archive(String ruleName, List<Metric> metrics) {
        for (Metric metric: metrics) {
            StringBuilder metricString = new StringBuilder();
            metricString.append("put").append(" ").append(ruleName)
                    .append(".")
                    .append(metric.getKey()).append(" ")
                    .append(new Date().getTime()/1000).append(" ")
                    .append(metric.getValue()).append(" ")
                    .append(metric.prepareTsdbTagsAsString())
                    .append("\n");
            queue.add(metricString.toString());
        }
    }

    @Override
    public String retrieveImg(String ruleName, String metricName, Map<String, Object> tags) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private ChannelFuture[] establishConnections(ClientBootstrap clientBootstrap, int numConnections, List<String> hostList, int port) {
        ChannelFuture[] channelFuture = new ChannelFuture[numConnections*hostList.size()];

        for (int i = 0; i < numConnections*hostList.size(); i++)  {
            for(String host : hostList)  {
                channelFuture[i] = clientBootstrap.connect(new InetSocketAddress(host, port), null);
                channelFuture[i].awaitUninterruptibly();
                if (channelFuture[i].isSuccess()) {
                    logger.info("Connection #" + i + " success, for host : " + host);
                }
                else {
                    logger.info("Connection #" + i + " failed, " + channelFuture[i].getCause() + " for host : " + host);
                }
                i++;
            }

            i--;
        }
        logger.info("openTSDB connections established");
        return channelFuture;
    }

    private ClientBootstrap configureNettyServer() throws Exception {
        if( nettyClient == null)  {
            ThreadPoolExecutor bossExecutor =  new ThreadPoolExecutor(1, // core size
                    10, // max size
                    60, // idle timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(100));

            ThreadPoolExecutor workerExecutor =  new ThreadPoolExecutor(100, // core size
                    100, // max size
                    60, // idle timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(500));

            ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossExecutor,workerExecutor);

            ClientBootstrap server = new ClientBootstrap(channelFactory);

            ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory()
            {
                public ChannelPipeline getPipeline() throws Exception
                {
                    return Channels.pipeline(new ChannelUpstreamHandler(),new StringEncoder());
                    //TODO: Add own upstream handler
                }
            };

            server.setPipeline(pipelineFactory.getPipeline());
            server.setPipelineFactory(pipelineFactory);

            this.nettyClient = server;
        }
        return  this.nettyClient;
    }

    private class OpenTSDBWriter implements Runnable {
        public void run() {
            List<String> openTSDBDataList = new ArrayList<String>();
            try {
                queue.drainTo(openTSDBDataList);
                synchronized (queueMutex) {
                    if(!openTSDBDataList.isEmpty()) {

                        Channel channel = getChannel();
                        if(channel != null) {
                            for(String data : openTSDBDataList) {
                                if(data != null && !data.isEmpty()) {
                                    channel.write(data);
                                }
                            }
                        }
                    }
                }
                openTSDBDataList.clear();
            }
            catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
            finally {
                queue.clear();
            }
        }

        private Channel getChannel() {
            if(channelFutures != null) {
                return channelFutures[0 + (int)(Math.random() * ((channelFutures.length - 1) + 1))].getChannel();
            }
            return null;
        }
    }
}
