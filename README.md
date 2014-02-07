+![Architecture](https://github.com/Flipkart/fk-alert-service/blob/master/alertz.png)
<br>fl-alert-service is a light weight Quartz based Alerting Rule scheduling system which allows users to fetch metrics from data sources like Graphite, Opentsdb and run user defined rules on them for breaches.
<br>
<br>
<br>===============
<br><b>Scheduling Rule</b>
<br>===============<br>
To mine metrics from Metric collection system like Opentsdb or graphite you have to create jobs in fk-alert-system.<br>Http Operation to do the same :<br>
HTTP POST on http://localhost:8080/fk-alert-service/scheduledRules with following request body :<br>
<pre>
     {
         "name" : "r1",
         "team" : "t1",
         "dataSerieses":
                 [
                     {
                         "name": "series1",
                         "source": "w3.graphite",
                         "query": "from=-2hours&until=now&height=750&title=METRIC_2_Unavailability_Ratio_percentage&target=legendValue%28alias%28asPercent%28divideSeries%28summarize%28statsd.w3.website.core_metrics.OOSProduct._all.Count.per_min%2C%2210min%22%29%2Csummarize%28statsd.w3.website.core_metrics.pageView._all.Count.per_min%2C%2210min%22%29%29%2C1%29%2C%22OOS%2BDiscontinued%20To%20Total%20PV%3A%20Last%20Value%20%28%25%29%3A%20%22%29%2C%22last%22%29&uniq=0.13684863388497093&format=json"
                     }
                 ],
         "checks":
                 [
                     {
                         "description" :"desc1",
                         "booleanExpression": "$last.series1 > $avg.series1",
                         "alertLevel": "1"
                     }

                 ],
         "schedule": {
             "interval": "1m",
             "startDate":"2012-11-10",
             "endDate":"2015-12-15",
             "days":"WEEKDAY",
             "times":"10:00:00-22:00:00,23:00:00-23:59:59",
             "dates" : "2012/11/11-2012/11/30,2012/12/01-2014/12/14"
         }
     }

</pre>
<br>
* Above rule would run every minute, fetch "series1" from "w3.graphite" based on the specified query. Then it would run list of checks mentioned above.
* booleanExpression should be a valid java expression
<br>
<br>
<br>===============
<br><b>Rule Status</b>
<br>===============<br>
To check the status of the rule fire following command :
<br>HTTP GET on http://localhost:8080/fk-alert-service/scheduledRules/1/status
<br>OR with schedule name
<br>HTTP GET on http://localhost:8080/fk-alert-service/scheduledRules/r1/status
<pre>
Output :
    {
       "status": "NORMAL",
       "description": null,
       "nextFireTime": "Wed Jan 16 18:45:00 IST 2013",
       "previousFireTime": "Wed Jan 16 18:44:00 IST 2013",
       "jobClass": "flipkart.alert.schedule.job.RuleJob"
    }
</pre>

<br>
<br>
<br>===============
<br><b>Pause/Resume Rule</b>
<br>===============<br>
If you need to pause to rule execution for a while execute following
<br>HTTP PUT on http://localhost:8080/fk-alert-service/scheduledRules/1/pause
<br>OR with schedule name
<br>HTTP PUT on http://localhost:8080/fk-alert-service/scheduledRules/r1/pause
<br>
<b>To Resume :</b>
<br>HTTP PUT on http://localhost:8080/fk-alert-service/scheduledRules/1/resume
<br>OR with schedule name
<br>HTTP PUT on http://localhost:8080/fk-alert-service/scheduledRules/r1/resume

<br>
<br>
<br>===============
<br><b>Rule Stats</b>
<br>===============<br>
How many times the rule has been executed and breached
<br>HTTP POST on http://localhost:8080/fk-alert-service/scheduledRules/1/stats
<br>OR with schedule name
<br>HTTP POST on http://localhost:8080/fk-alert-service/scheduledRules/r1/stats

<br>
<br>
<br>===============
<br><b>Update Rule</b>
<br>===============<br>
HTTP PUT on http://localhost:8080/fk-alert-service/scheduledRules/1 with following request body :<br>
<pre>
     {
         "name" : "r1",
         "team" : "t1",
         "dataSerieses":
                 [
                     {
                         "name": "series1",
                         "source": "w3.graphite",
                         "query": "from=-2hours&until=now&height=750&title=METRIC_2_Unavailability_Ratio_percentage&target=legendValue%28alias%28asPercent%28divideSeries%28summarize%28statsd.w3.website.core_metrics.OOSProduct._all.Count.per_min%2C%2210min%22%29%2Csummarize%28statsd.w3.website.core_metrics.pageView._all.Count.per_min%2C%2210min%22%29%29%2C1%29%2C%22OOS%2BDiscontinued%20To%20Total%20PV%3A%20Last%20Value%20%28%25%29%3A%20%22%29%2C%22last%22%29&uniq=0.13684863388497093&format=json"
                     }
                 ],
         "checks":
                 [
                     {
                         "description" :"desc1",
                         "booleanExpression": "$last.series1 > $avg.series1",
                         "alertLevel": "1"
                     }

                 ],
         "schedule": {
             "interval": "1m",
             "startDate":"2012-11-10",
             "endDate":"2015-12-15",
             "days":"WEEKDAY",
             "times":"10:00:00-22:00:00,23:00:00-23:59:59",
             "dates" : "2012/11/11-2012/11/30,2012/12/01-2014/12/14"
         }
     }

</pre>

<br>
<br>
<br>===============
<br><b>Delete Rule</b>
<br>===============<br>
How many times the rule has been executed and breached
<br>HTTP DELETE on http://localhost:8080/fk-alert-service/scheduledRules/1
<br>OR with schedule name
<br>HTTP DELETE on http://localhost:8080/fk-alert-service/scheduledRules/r1

<br>
<br>
<br>======================
<br><b>Creating Metric Sources</b>
<br>======================<br>
We used "w3.graphite" in creating above rule. System support addition of multiple graphite sources at runtime. And you can create using following command. This would be the first step in using this system if you haven't add any metric source so far or its a new system.
<br>
HTTP POST on http://localhost:8080/fk-alert-service/metricSources with following request body :<br>
<pre>
    {
        "name": "scm.graphite",
        "sourceType": "GRAPHITE",
        "sourceConnectionParams": [
            {
                "param":"graphiteHost",
                "value": "ops-statsd.nm.flipkart.com"
            },
            {
                "param":"graphitePort",
                "value": "80"
            }
        ]
    }
</pre>

<br>
<br>
<br>======================
<br><b>Get Metric Source</b>
<br>======================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/metricSources/1
You can also use HTTP GET on http://localhost:8080/fk-alert-service/metricSources/scm.graphite

<br>
<br>
<br>======================
<br><b>Get All Metric Sources</b>
<br>======================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/metricSources

<br>
<br>
<br>======================
<br><b>Updating Metric Source</b>
<br>======================<br>
HTTP PUT on http://localhost:8080/fk-alert-service/metricSources/1 with following request body :<br>
<pre>
    {
        "name": "scm.graphite",
        "sourceType": "GRAPHITE",
        "sourceConnectionParams": [
            {
                "param":"graphiteHost",
                "value": "ops-statsd.nm.flipkart.com"
            },
            {
                "param":"graphitePort",
                "value": "80"
            }
        ]
    }
</pre>
<br>
You can also use HTTP PUT on http://localhost:8080/fk-alert-service/metricSources/scm.graphite with following request body :<br>

<br>
<br>
<br>======================
<br><b>Delete Metric Source</b>
<br>======================<br>
<br>HTTP DELETE on http://localhost:8080/fk-alert-service/metricSources/1
You can also use HTTP DELETE on http://localhost:8080/fk-alert-service/metricSources/scm.graphite

<br>
<br>
<br>==================================================================
<br><b>Get All Metrics that are being fetched by various rules</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/metrics
<pre>
Output:
    [
       "first.series1",
       "max.series1",
       "min.series1",
       "avg.series1",
       "last.series1",
       "sum.series1"
    ]
</pre>

<br>
<br>
<br>==================================================================
<br><b>Get Last Value of particular metric</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/metrics/avg.series1
<pre>
Output:
    [
       {
           "metric":
           {
               "key": "avg.series1",
               "value": 8.070975615435813,
               "metricTags": null,
               "metricSource": null,
               "creationTime": 1358342640362
           },
           "rulesBreached":
           [
               {
                   "ruleId": 1,
                   "name": "r1",
                   "team": "t1",
                   "dataSerieses":
                   [
                       {
                           "name": "series1",
                           "source": "w3.graphite",
                           "query": "from=-2hours&until=now&height=750&title=METRIC_2_Unavailability_Ratio_percentage&target=legendValue%28alias%28asPercent%28divideSeries%28summarize%28statsd.w3.website.core_metrics.OOSProduct._all.Count.per_min%2C%2210min%22%29%2Csummarize%28statsd.w3.website.core_metrics.pageView._all.Count.per_min%2C%2210min%22%29%29%2C1%29%2C%22OOS%2BDiscontinued%20To%20Total%20PV%3A%20Last%20Value%20%28%25%29%3A%20%22%29%2C%22last%22%29&uniq=0.13684863388497093&format=json"
                       }
                   ],
                   "checks":
                   [
                       {
                           "description": "desc1",
                           "booleanExpression": "$last.series1 > $avg.series1",
                           "alertLevel": "1"
                       }
                   ],
                   "schedule":
                   {
                       "startDate": 1352505600000,
                       "endDate": 1450137600000,
                       "dates": "2012/11/11-2012/11/30,2012/12/01-2014/12/14",
                       "days": "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY",
                       "times": "10:00:00-22:00:00,23:00:00-23:59:59",
                       "interval": "1m"
                   },
                   "alertQueue": "t1-r1"
               }
           ]
       }
    ]

</pre>
* To get multiple instances use "count" query parameter

<br>
<br>
<br>==================================================================
<br><b>Get Metric instance with last alert</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/metrics/avg.series1/lastAlert

<br>
<br>
<br>==================================================================
<br><b>Check Generated Alerts</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/alerts
<pre>
Output
    {
       "r1-t1": 14
    }

Key is combination of ruleName-teamName
</pre>

<br>
<br>
<br>==================================================================
<br><b>Get Last Alert</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/alerts/queues/r1-t1
<pre>
Output
    [
       {
           "metrics":
           [
               {
                   "key": "last.series1",
                   "value": 9.924812030075188,
                   "metricTags": null,
                   "metricSource": null,
                   "creationTime": 1358342040484
               },
               {
                   "key": "avg.series1",
                   "value": 7.931044243998089,
                   "metricTags": null,
                   "metricSource": null,
                   "creationTime": 1358342040484
               }
           ],
           "rule":
           {
               "ruleId": 1,
               "name": "r1",
               "team": "t1",
               "dataSerieses":
               [
                   {
                       "name": "series1",
                       "source": "w3.graphite",
                       "query": "from=-2hours&until=now&height=750&title=METRIC_2_Unavailability_Ratio_percentage&target=legendValue%28alias%28asPercent%28divideSeries%28summarize%28statsd.w3.website.core_metrics.OOSProduct._all.Count.per_min%2C%2210min%22%29%2Csummarize%28statsd.w3.website.core_metrics.pageView._all.Count.per_min%2C%2210min%22%29%29%2C1%29%2C%22OOS%2BDiscontinued%20To%20Total%20PV%3A%20Last%20Value%20%28%25%29%3A%20%22%29%2C%22last%22%29&uniq=0.13684863388497093&format=json"
                   }
               ],
               "checks":
               [
                   {
                       "description": "desc1",
                       "booleanExpression": "$last.series1 > $avg.series1",
                       "alertLevel": "1"
                   }
               ],
               "schedule":
               {
                   "startDate": 1352505600000,
                   "endDate": 1450137600000,
                   "dates": "2012/11/11-2012/11/30,2012/12/01-2014/12/14",
                   "days": "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY",
                   "times": "10:00:00-22:00:00,23:00:00-23:59:59",
                   "interval": "1m"
               },
               "alertQueue": "t1-r1"
           },
           "breachedCheck":
           {
               "description": "desc1",
               "booleanExpression": "$last.series1 > $avg.series1",
               "alertLevel": "1"
           }
       }
    ]
</pre>
<br>
<br>
<br>==================================================================
<br><b>Get Last Alert in Nagios Format</b>
<br>==================================================================<br>
<br>HTTP GET on http://localhost:8080/fk-alert-service/alerts/queues/r1-t1/formats/NAGIOS
<pre>
Output
    {
       "message": "Metrics (last.series1=9.924812030075188,avg.series1=7.931044243998089) breached check ($last.series1 > $avg.series1)",
       "alertLevel": "1"
    }
</pre>

<br>=================
<br>Local Deployment
<br>=================
<br>1) <b>Environment</b> :Make sure you have sun-java6, maven installed
<br>2) <b>Clone fk-alert-service</b> : git@github.com:Flipkart/fk-alert-service.git
<br>3) <b>create database 'fk_alert_service'</b> ./db/recreate_db.sh fk_alert_service
<br>4) Change DB username and password if required in src/main/resources/quartz.properties and src/main/resources/hibernate.cfg.xml
<br>5) <b>Build project assembly</b> : mvn clean compile assembly:assembly
<br>6) <b>Start Service</b>  :java -jar target/fk-alert-service-1.0-SNAPSHOT-jar-with-dependencies.jar


<br>======================
<br>App Production Deployment
<br>======================
<br>1) <b>Clone fk-alert-service</b> : git@github.com:Flipkart/fk-alert-service.git
<br>2) <b>cd DEBIAN</b>
<br>3) <b>Change product versions in create_fk-alert-service_deb.sh </b>
<br>4) <b>./create_fk-alert-service_deb.sh </b>
<br>5) <b>Push To Prod Repo </b> :Follow Steps in https://wiki.corp.flipkart.com/index.php/Upload_arbitrary_package_to_FLO_apt-repo to upload the generated debian package to prod repo.
<br>6) <b>Install</b> : Go to alertz-app1.nm.flipkart.com. sudo apt-get update. sudo apt-get remove fk-alert-service. sudo apt-get install fk-alert-service
<br>7) <b>If its first time</b> : sudo /etc/init.d/fk-alert-service create_db and then sudo /etc/init.d/fk-alert-service start

<br>======================
<br>Nagios Crontab Entry Production Deployment
<br>======================
<br>1) <b>Clone fk-alert-poll-cron</b> : git@github.com:Flipkart/fk-alert-poll-cron.git
<br>2) <b>New Crontab :</b> Add here crontab/alert_poll_crons
<br>3) <b>Change product versions in create_fk-alert-poll-cron_deb.sh </b>
<br>4) <b>./create_fk-alert-poll-cron_deb.sh </b>
<br>5) <b>Push To Prod Repo </b> :Follow Steps in https://wiki.corp.flipkart.com/index.php/Upload_arbitrary_package_to_FLO_apt-repo to upload the generated debian package to prod repo.
<br>6) <b>Install</b> : Go to alertz-app1.nm.flipkart.com. sudo apt-get update. sudo apt-get remove fk-alert-poll-cron. sudo apt-get install fk-alert-poll-cron


