package com.flipkart.alert.storage.archiver;

import com.flipkart.alert.domain.Metric;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.core.Clock;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.*;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * nitink.agarwal@yahoo.com
 */
public class RRD4JFileMetricArchiver extends MetricArchiver{
    private String rrdBasePath;
    private Map<String, String> metricPathMap;
    private Map<String, String> metricDataSourceMap;
    private Map<String, RrdDb> metricRddDBMap;
    public static final String RRD_BASE_PATH = "basePath";

    public RRD4JFileMetricArchiver(Map<String, Object> params) throws Exception {
        super(params);
        metricPathMap = new LinkedHashMap<String, String>();
        metricRddDBMap = new LinkedHashMap<String, RrdDb>();
        metricDataSourceMap = new HashMap<String, String>();

        File rrdBasePath = new File(this.rrdBasePath = params.get(RRD_BASE_PATH).toString());
        if(!rrdBasePath.exists())
            rrdBasePath.mkdirs();

        for(File metricSourceFile : rrdBasePath.listFiles()) {
            if(metricSourceFile.getName().endsWith(".rrd")) {
                String metricName = metricSourceFile.getName().replace(".rrd", "");
                metricPathMap.put(metricName, metricSourceFile.getAbsolutePath());
                RrdDb rrdDb = new RrdDb(metricSourceFile.getAbsolutePath());
                metricRddDBMap.put(metricName, rrdDb);
                metricDataSourceMap.put(metricName,rrdDb.getRrdDef().getDsDefs()[0].getDsName());
            }
        }
    }

    @Override
    public void archive(String ruleName, List<Metric> metrics) throws IOException {
        for(Metric metric : metrics) {
            String metricName = ruleName + "." + metric.getKey();
            if(!metricRddDBMap.containsKey(metricName)) {

                String newDataSourceNameForMetric = getUniqueRandomDataSourceName();
                String metricRRDFilePath = this.rrdBasePath + File.separator + metricName + ".rrd";

                DsDef dsDef = new DsDef(newDataSourceNameForMetric, DsType.GAUGE, 600, Double.NaN, Double.NaN);
                RrdDef rrdDef = new RrdDef(metricRRDFilePath);
                rrdDef.addDatasource(dsDef);
                rrdDef.setStartTime((int) ((metric.getCreationTime().getTime()) / 1000));
                    /*
                    Following Archives should be based on Metric Data Type. Keeping it as is for the time being
                     */
                // Assuming every point comes in 30 seconds
                rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 2 * 60 * 24 * 1); // archiving it for 1 day
                rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 10, 12 * 24 * 7); // Archiving for 7 days
                rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 120, 24 * 30); // archiving for 30 days
                rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 3600, 30 * 12 * 2); // archiving roughly for 2 years

                rrdDef.addArchive(ConsolFun.TOTAL, 0.5, 1, 2 * 60 * 24 * 1); // archiving it for 1 day
                rrdDef.addArchive(ConsolFun.TOTAL, 0.5, 10, 12 * 24 * 7); // Archiving for 7 days
                rrdDef.addArchive(ConsolFun.TOTAL, 0.5, 120, 24 * 30); // archiving for 30 days
                rrdDef.addArchive(ConsolFun.TOTAL, 0.5, 3600, 30 * 12 * 2); // archiving roughly for 2 years

                rrdDef.addArchive(ConsolFun.MAX, 0.5, 1, 2 * 60 * 24 * 1); // archiving it for 1 day
                rrdDef.addArchive(ConsolFun.MAX, 0.5, 10, 12 * 24 * 7); // Archiving for 7 days
                rrdDef.addArchive(ConsolFun.MAX, 0.5, 120, 24 * 30); // archiving for 30 days
                rrdDef.addArchive(ConsolFun.MAX, 0.5, 3600, 30 * 12 * 2); // archiving roughly for 2 years

                RrdDb rrdDb = new RrdDb(rrdDef);

                metricRddDBMap.put(metricName, rrdDb);
                metricPathMap.put(metricName, metricRRDFilePath);
                metricDataSourceMap.put(metricName, newDataSourceNameForMetric);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                metric.setCreationTime(new Date());
            }

            RrdDb rrdDb = new RrdDb(metricPathMap.get(metricName));
            Sample sample = rrdDb.createSample();
            logger.info("Archiving :"+new ObjectMapper().writeValueAsString(metric));
            sample.setAndUpdate((int)(metric.getCreationTime().getTime()/1000) + ":" +metric.getValue());
            rrdDb.close();
        }
    }

    private String getUniqueRandomDataSourceName() {
        String uniqueString = RandomStringUtils.randomAlphanumeric(20);
        while(metricDataSourceMap.values().contains(uniqueString))
            uniqueString = RandomStringUtils.randomAlphanumeric(20);
        return uniqueString;
    }

    @Override
    public InputStream retrieve(String ruleName, List<String> metrics, METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException {
        ConsolFun consoleFun = ConsolFun.MAX;
        if(metric_type.equals(METRIC_TYPE.BREACH))
            consoleFun = ConsolFun.TOTAL;

        long startTimeSec = startTime.getTime()/1000;
        long endTimeSec = endTime.getTime()/1000;

        RrdGraphDef graphDef = new RrdGraphDef();
        graphDef.setTimeSpan(startTimeSec, endTimeSec);
        graphDef.setTitle("Rule " + ruleName + " Metrics");
        String imageFilePath = "/tmp/"+ruleName+"-"+System.nanoTime()+".ig";
        graphDef.setFilename(imageFilePath);

        for(int i=0;i<metrics.size();i++) {
            String metric = metrics.get(i);
            graphDef.datasource(metric, metricPathMap.get(metric), metricDataSourceMap.get(metric), consoleFun);
            graphDef.line(metric, new Color(20000 * i), metric, 2);

        }
        BufferedImage bi = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
        RrdGraph graph = new RrdGraph(graphDef);
        graph.render(bi.getGraphics());
        return new FileInputStream(imageFilePath);
    }

    @Override
    public Map<String, List<MetricInstance>> retrieveRaw(String ruleName, List<String> metrics, METRIC_TYPE metric_type, Date startTime, Date endTime) throws IOException {
        ConsolFun consoleFun = ConsolFun.MAX;
        if(metric_type.equals(METRIC_TYPE.BREACH))
            consoleFun = ConsolFun.TOTAL;

        long startTimeSec = startTime.getTime()/1000;
        long endTimeSec = endTime.getTime()/1000;

        Map<String, List<MetricInstance>> metricsRaw = new LinkedHashMap<String, List<MetricInstance>>();

        for(String metric : metrics) {
            RrdDb rrdDb = metricRddDBMap.get(metric);
            FetchRequest fetchRequest = rrdDb.createFetchRequest(consoleFun, startTimeSec, endTimeSec);
            FetchData fetchData = fetchRequest.fetchData();
            String dump = fetchData.dump();
            if(dump != null && !dump.trim().equals("")) {
                String[] rows = dump.split("\n");
                List<MetricInstance> metricInstanceList = new LinkedList<MetricInstance>();
                metricsRaw.put(metric, metricInstanceList);

                for(String row : rows) {
                    String[] rowTokens = row.split(": ");
                    try {
                        metricInstanceList.add(new MetricInstance(Long.parseLong(rowTokens[0]), Double.parseDouble(rowTokens[1])));
                    }
                    catch(NumberFormatException e) {
                        logger.error("", e);
                    }
                }
            }

        }
        return metricsRaw;
    }
}
