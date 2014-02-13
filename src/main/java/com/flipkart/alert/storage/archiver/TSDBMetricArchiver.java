package com.flipkart.alert.storage.archiver;

import com.flipkart.alert.util.DateHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.Metric;
import com.flipkart.alert.storage.ChannelUpstreamHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.io.*;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.*;
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

    private List <String> hosts;
    private final Integer defaultPort = 4242;
    private BlockingQueue <String> queue = new ArrayBlockingQueue<String>(10000);
    private ChannelFuture[] channelFutures;
    private final Object queueMutex = new Object();

    private ClientBootstrap writerClient = null;
    private AsyncHttpClient readerClient;

    public TSDBMetricArchiver(Map<String, Object> params) throws Exception {
        super(params);
        hosts = (List<String>) params.get("hosts");
        writerClient = configureNettyServer();
        readerClient = new AsyncHttpClient();
        channelFutures = establishConnections(writerClient,10, hosts, defaultPort);

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
    public InputStream retrieve(String ruleName, List<String> metrics, METRIC_TYPE metric_type, Date startTime, Date endTime) {
        String aggregateFunction = "max";
        if(metric_type.equals(METRIC_TYPE.BREACH))
            aggregateFunction = "max:60m-sum";

        try {
            String startTimeStr = DateHelper.format(startTime, "yyyy/MM/dd-HH:mm:ss");
            String endTimeStr = DateHelper.format(endTime, "yyyy/MM/dd-HH:mm:ss");;
            for(String host : hosts) {
                try {
                    StringBuilder url = new StringBuilder("http://" + host + ":" + defaultPort
                            + "/q?start=" + startTimeStr + "&end=" + endTimeStr);
                    for(String metric: metrics) {
                        url.append("&o=&m=" + aggregateFunction + ":" + metric);
                    }
                    url.append("&wxh=600x300&png");
                    Future<Response> fResponse= readerClient.prepareGet(url.toString()).execute();
                    Response response = fResponse.get();
                    return response.getResponseBodyAsStream();
                } catch (IOException e) {
                    logger.error("Error while contact TSDB service", e);
                    continue;
                } catch (InterruptedException e) {
                    logger.error("Error while contact TSDB service", e);
                    break;
                } catch (ExecutionException e) {
                    logger.error("Error while contact TSDB service", e);
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    @Override
    public Map<String, List<MetricInstance>> retrieveRaw(String ruleName, List<String> metrics, METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException {
        String aggregateFunction = "max";
        if(metric_type.equals(METRIC_TYPE.BREACH))
            aggregateFunction = "max:60m-sum";

        try {
            String startTimeStr = DateHelper.format(startTime, "yyyy/MM/dd-HH:mm:ss");
            String endTimeStr = DateHelper.format(endTime, "yyyy/MM/dd-HH:mm:ss");;
            Map<String, List<MetricInstance>> rawMetrics = new LinkedHashMap<String, List<MetricInstance>>();
            for(String host : hosts) {
                try {
                    StringBuilder url = new StringBuilder("http://" + host + ":" + defaultPort
                            + "/q?start=" + startTimeStr + "&end=" + endTimeStr);
                    for(String metric: metrics) {
                        url.append("&o=&m=" + aggregateFunction + ":" + metric);
                    }
                    url.append("&wxh=600x300&ascii");
                    Future<Response> fResponse= readerClient.prepareGet(url.toString()).execute();
                    Response response = fResponse.get();

                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));
                    String line = null;
                    while((line = br.readLine())!= null) {
                        String[] tokens = line.split(" ");
                        List<MetricInstance> metricInstancesList = rawMetrics.get(tokens[0]);
                        if(metricInstancesList == null) {
                            rawMetrics.put(tokens[0], metricInstancesList);
                        }
                        metricInstancesList.add(new MetricInstance(Long.parseLong(tokens[1]), Double.parseDouble(tokens[2])));
                    }
                    return rawMetrics;
                } catch (IOException e) {
                    logger.error("Error while contact TSDB service", e);
                    continue;
                } catch (InterruptedException e) {
                    logger.error("Error while contact TSDB service", e);
                    break;
                } catch (ExecutionException e) {
                    logger.error("Error while contact TSDB service", e);
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
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
        if( writerClient == null)  {
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

            this.writerClient = server;
        }
        return  this.writerClient;
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
