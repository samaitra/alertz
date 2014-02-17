/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 02/08/13
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */


function getMetricSource(sourceName) {
    var url = '/alertz/metricSources/' + sourceName;
    var response;
    $.ajax({
                type: 'GET',
                url: url,
                async: false,
                cache: false,
                contentType:'application/json',
                success: function (jsonData) {
                    response = jsonData;
                },
                error: function(err) {
                    defineErrorType(err, "Failed to fetch metric source!")
                }
            });
    return response;
}

function getMetricSourceNames(){
    var sources = getMetricSources();
    var sourceNames = [];
    $.each(sources, function(index, data) {
        sourceNames.push(data.name);
    });
    return sourceNames;
}


function getMetricSources(){
    var url = '/alertz/metricSources/';
    var response;
    $.ajax({
                type: 'GET',
                url: url,
                async: false,
                cache: false,
                contentType:'application/json',
                success: function (jsonData) {
                    response = jsonData;
                },
                error: function(err) {
                    defineErrorType(err, "Failed to fetch metric source!")
                }
            });
    return response;
}