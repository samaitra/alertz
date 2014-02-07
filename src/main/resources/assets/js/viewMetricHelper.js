/**
 * Created by IntelliJ IDEA.
 * User: shashankg
 * Date: 19/11/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */

function getAsciiValueForBreachFromTsdb(ruleName, fromTime, toTime) {
    var dataArchival = getDataArchivalSource();
    var allHosts = dataArchival.hosts;
    var host = allHosts[Math.floor(Math.random() * allHosts.length)];
    var port = "4242";
    var url = "http://" + host + ":" + port + "/q?start=" + fromTime + "&end=" + toTime + "&m=max:60m-sum:" + ruleName + ".breached" + "&o=&ascii";
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'text/plain;charset=UTF-8',
        success: function (jsonData) {
            response = jsonData;
        }
    });
    return response;
}

function getAsciiValueForCheckFromTSDB(ruleName, fromTime, toTime, content) {
    var dataArchival = getDataArchivalSource();
    var allHosts = dataArchival.hosts;
    var host = allHosts[Math.floor(Math.random() * allHosts.length)];
    var port = "4242";
    var url = "http://" + host + ":" + port + "/q?start=" + fromTime + "&end=" + toTime + "&m=max:" + ruleName + "." + content["name"] + "&o=&ascii";
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'text/plain;charset=UTF-8',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            console.log("Error while fetching data from TSDB!");
        }
    });
    return response;
}

function getMinOfKeyFromAscii(asciiValue) {
    var arr = getKeyArrayFromAscii(asciiValue);
    return getMinOfArray(arr);
}

function getMaxOfArray(numArray) {
    return (numArray[0] != undefined ? Math.max.apply(null, numArray) : -1);
}

function getMinOfArray(numArray) {
    return (numArray[0] != undefined ? Math.min.apply(null, numArray) : -1);
}

function getPercentileForResponseTime(asciiValue, threshold) {
    var arr = getKeyArrayFromAscii(asciiValue);
    var percentileArr = [];
    for (var index = 0; index < arr.length; index++)
        if (arr[index] >= threshold) {
            percentileArr.push(arr[index]);
        }

    percentileArr.sort(function(a, b) {
        return a - b
    });

    var targetIndex = parseInt(percentileArr.length * 0.8);
    return percentileArr[targetIndex];
}

function getBreachMinRange(asciiValue, threshold, breachMax) {
    var breachArr = getKeyArrayFromAscii(asciiValue);
    breachArr.sort(function(a, b) {
        return a - b
    });
    for (var index = 0; index <= breachArr.length; index++) {
        var element = breachArr[index];
        if (element >= threshold) {
            if (element == breachMax)
                return threshold;
            else
                return element;
        }
    }
    return threshold;
}

function getLabelClass(breachCount, fromTime, toTime) {
    var toDate = new Date(toTime.split("-")[0]);
    var fromDate = new Date(fromTime.split("-")[0]);
    var days = (toDate - fromDate) / 86400000;
    var breachCountPerDay = breachCount / days;
    if (breachCountPerDay >= breachThreshold)
        return "label label-important";
    else if (breachCountPerDay >= (breachThreshold / 2) && breachCountPerDay < breachThreshold)
        return "label label-warning";
    else if (breachCountPerDay < (breachThreshold / 2) && breachCountPerDay >= 1)
        return "label label-info";
    else
        return "label label-success";
}

function getResponseTimeThreshold(ruleId) {
    var responseTimeThreshold;
    var statResp = getRuleStat(ruleId);
    $.each(statResp.checkStats, function(k, v) {
        if (v.expression.match("responseTime") != null) {
            var arr = v.expression.split(">");
            responseTimeThreshold = arr[1];
        }
    });
    return responseTimeThreshold;
}

function setHighChartTheme() {
    return Highcharts.theme = {
        colors: ['#31B404', '#58ACFA', '#FE9A2E', '#DF0101'],
        chart: {
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
                stops: [
                    [0, 'rgb(255, 255, 255)'],
                    [1, 'rgb(240, 240, 255)']
                ]
            },
            borderWidth: 2,
            plotBackgroundColor: 'rgba(255, 255, 255, .9)',
            plotShadow: true,
            plotBorderWidth: 1
        },
        title: {
            style: {
                color: '#000',
                font: 'bold 14px "Trebuchet MS", Verdana, sans-serif'
            }
        },
        subtitle: {
            style: {
                color: '#666666',
                font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
            }
        },
        xAxis: {
            gridLineWidth: 1,
            lineColor: '#000',
            tickColor: '#000',
            labels: {
                style: {
                    color: '#000',
                    font: '11px Trebuchet MS, Verdana, sans-serif'
                }
            },
            title: {
                style: {
                    color: '#333',
                    fontWeight: 'bold',
                    fontSize: '12px',
                    fontFamily: 'Trebuchet MS, Verdana, sans-serif'

                }
            }
        },
        yAxis: {
            minorTickInterval: 'auto',
            lineColor: '#000',
            lineWidth: 1,
            tickWidth: 1,
            tickColor: '#000',
            labels: {
                style: {
                    color: '#000',
                    font: '11px Trebuchet MS, Verdana, sans-serif'
                }
            },
            title: {
                style: {
                    color: '#333',
                    fontWeight: 'bold',
                    fontSize: '12px',
                    fontFamily: 'Trebuchet MS, Verdana, sans-serif'
                }
            }
        },
        legend: {
            itemStyle: {
                font: '9pt Trebuchet MS, Verdana, sans-serif',
                color: 'black'

            },
            itemHoverStyle: {
                color: '#039'
            },
            itemHiddenStyle: {
                color: 'gray'
            }
        },
        labels: {
            style: {
                color: '#99b'
            }
        },

        navigation: {
            buttonOptions: {
                theme: {
                    stroke: '#CCCCCC'
                }
            }
        }
    };
}

function drawHighChart(successInput, infoInput, warningInput, dangerInput, title, subtitle) {
    var highchartsOptions = Highcharts.setOptions(setHighChartTheme());
    var chart;
    $(document).ready(function () {
        $('#container').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: title
            },
            subtitle: {
                text: subtitle
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true
                }
            },
            series: [
                {
                    type: 'pie',
                    name: 'Share',
                    data: [
                        {
                            name: 'Breaches less than 1/day',
                            y: successInput,
                            sliced: true,
                            selected: true
                        },
                        ['Breaches ranging 1-5/day',   infoInput],
                        ['Breaches ranging 5-10/day',   warningInput],
                        ['Breaches greater than 10/day',    dangerInput]
                    ]
                }
            ]
        });
    });
}
