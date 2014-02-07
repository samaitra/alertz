/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 07/08/13
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */


var getRuleByNameUrl = '/fk-alert-service/scheduledRules/$ruleName';
var updateRuleUrl = '/fk-alert-service/scheduledRules/$ruleId';

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");
    var ruleName = getParam("ruleName");
    initDatePicker();
    $('#ruleTemplate').load("/ruleTemplate.html", function(){
        initPopOver();
        ko.applyBindings(new UpdateRuleModel(ruleName));
    });
});


function UpdateRuleModel(ruleName) {
    var self = this;
    self.content = getRuleByName(ruleName);
    self.jsonData = ko.observable(self.content);

    self.content.name = ko.observable(self.content.name);
    self.content.team = ko.observable(self.content.team);
    self.content.dataSerieses = ko.observableArray(self.content.dataSerieses);

    self.content.addDataSeries = function() {
        self.content.dataSerieses.push({
            name: "",
            source: "",
            query: ""
        });
    };

    self.content.removeDataSeries = function() {
        self.content.dataSerieses.pop();
    };

    self.content.variables = ko.observableArray(self.content.variables);
    self.content.addVariables = function() {
        self.content.variables.push({
            name: "",
            value: ""
        });
    };

    self.content.removeVariables = function() {
        self.content.variables.pop();
    };

    self.content.checks = ko.observableArray(self.content.checks);
    self.content.addChecks = function() {
        self.content.checks.push({
            description: "",
            booleanExpression: "",
            alertLevel:""
        });
    };

    self.content.removeChecks = function() {
        self.content.checks.pop();
    };

    self.content.endPoints = ko.observableArray(self.content.endPoints);

    $.each(self.content.endPoints(), function(i, data){
        self.content.endPoints()[i].publishAlways = ko.observable(self.content.endPoints()[i].publishAlways.toString());
        self.content.endPoints()[i].endPointParams = ko.observableArray(self.content.endPoints()[i].endPointParams);

        self.content.endPoints()[i].changeContent = function(el) {
            console.log(el);
            self.content.endPoints()[i].type = el.type;
            self.content.endPoints()[i].publishAlways = ko.observable(false.toString());

            if(el.type == 'MAIL') {
               self.content.endPoints()[i].endPointParams([{"name":"to", value:""}]);
            } else {
                self.content.endPoints()[i].endPointParams([]);
            }
            console.log(self.content.endPoints());
        }

    });
    self.endPointTypes = ["MAIL","NAGIOS"];
    self.dataSources = getMetricSourceNames();

    self.content.addEndPoints = function() {
        self.content.endPoints.push({
            type: "NAGIOS",
            publishAlways: true,
            endPointParams: ko.observableArray([]),
            changeContent: function(el) {
                var i = self.content.endPoints().length-1;
                self.content.endPoints()[i].type = el.type;
                self.content.endPoints()[i].publishAlways = "false";

                if(el.type == 'MAIL') {
                    self.content.endPoints()[i].endPointParams([{"name":"to", value:""}]);
                } else {
                    self.content.endPoints()[i].endPointParams([]);
                }
            }
        });
    };

    self.content.removeEndPoints = function() {
        self.content.endPoints.pop();
    };

    self.content.schedule = ko.observable(self.content.schedule);
    self.content.schedule().startDate =  ko.observable($.datepicker.formatDate("yy-mm-dd", new Date(self.content.schedule().startDate)));
    self.content.schedule().endDate = ko.observable($.datepicker.formatDate("yy-mm-dd", new Date(self.content.schedule().endDate)));
    self.content.schedule().interval =  ko.observable(self.content.schedule().interval);
    self.content.schedule().dates = ko.observable(self.content.schedule().dates);
    self.content.schedule().days = ko.observable(self.content.schedule().days);
    self.content.schedule().times = ko.observable(self.content.schedule().times);

    self.content.schedule().startDateFormat = function(selected) {
        var newDate = new Date(selected);
        var formattedDate = $.datepicker.formatDate("yy-mm-dd", new Date(newDate.setDate(newDate.getDate() + 1)));
        $("#endDate").datepicker("option","minDate", formattedDate);
        self.content.schedule().startDate(selected);

    };
    self.content.schedule().endDateFormat = function(selected) {
        var newDate = new Date(selected);
        var formattedDate = $.datepicker.formatDate("yy-mm-dd", new Date(newDate.setDate(newDate.getDate() - 1)));
        $("#startDate").datepicker("option","maxDate", formattedDate);
        self.content.schedule().endDate(selected);
    };

    self.content.save = function(){
        var jsonDataOfModel = ko.toJSON(self.jsonData);
        console.log("JSON used for Update Rule", jsonDataOfModel);
        updateRule(self.content.ruleId, self.content.name(), jsonDataOfModel);
    };

    console.log(self);

}