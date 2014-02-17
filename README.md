![Architecture](https://github.com/Flipkart/alertz/raw/master/alertz.png)<br>

<br>fl-alertz is a light weight Quartz based Alerting Rule scheduling system which allows users to fetch metrics from data sources like Graphite, Opentsdb and run user defined rules on them for breaches.
<br>
<br>
<br>===============
<br><b>Getting Started</b>
<br>===============<br>
<br>1) <b>Environment</b> :Make sure you have sun-java6, maven installed
<br>2) <b>Clone alertz</b> : git@github.com:Flipkart/alertz.git
<br>3) <b>create database 'alertz'</b> ./db/recreate_db.sh alertz
<br>4) Change DB username and password if required in src/main/resources/quartz.properties and src/main/resources/hibernate.cfg.xml
<br>5) <b>Compile project assembly</b> : mvn clean compile
<br>6) <b>Start Service</b>  :mvn clean compile exec:java

<br>===============
<br><b>Look Around</b>
<br>===============<br>
<br>1) Go to http://localhost:8888/index.html, This is the starting page of Alertz Service. Read Through to get acquainted with the application.
<br>1) Go to http://localhost:8888/getStarted.html, This page will help you create rules.<br>
But before you create the rules, you need to provide some connection information about your data source (graphite/ tsdb) which you will use in your rule to pull data).
<pre>
Do Http POST on http://localhost:8888/alertz/metricSources
{"name":"flo.graphite","sourceType":"GRAPHITE","sourceConnectionParams":[{"param":"graphitePort","value":"80"},{"param":"graphiteHost","value":"Graphite Host Name"}]}
</pre>
