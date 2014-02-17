/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 27/07/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */

var createRuleUrl = '/alertz/scheduledRules';
var getTeamsUrl = '/alertz/teams/scheduledRules';

$(document).ready(function () {
    $("#teams").typeahead({source: getTeams()});
    $('#headerPlaceHolder').load("/header.html");
    initDatePicker();
    initPopOver();
    $('#ruleTemplate').load("/ruleTemplate.html", function(){
        ko.applyBindings(new CreateRuleModel());
    });
});


function CreateRuleModel() {
    var self = this;
    self.content = {};
    self.jsonData = ko.observable(self.content);

    self.content.name = ko.observable();
    self.content.team = ko.observable();
    self.content.dataSerieses = ko.observableArray([{
        name: "",
        source: "",
        query: ""
    }]);

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

    self.content.variables = ko.observableArray([{
        name: "",
        value: ""
    }]);
    self.content.addVariables = function() {
        self.content.variables.push({
                    name: "",
                    value: ""
                });
    };

    self.content.removeVariables = function() {
        self.content.variables.pop();
    };

    self.content.checks = ko.observableArray([{
            description: "",
            booleanExpression: "",
            alertLevel:""
        }]);
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

    self.content.endPoints = ko.observableArray([{
                    type: "NAGIOS",
                    publishAlways: true,
                    endPointParams: ko.observableArray([]),
                    changeContent: function(el) {
                        var i = 0;
                        self.content.endPoints()[i].type = el.type;
                        self.content.endPoints()[i].publishAlways = false;

                        if(el.type == 'MAIL') {
                            self.content.endPoints()[i].endPointParams([{"name":"to", value:""}]);
                        } else {
                            self.content.endPoints()[i].endPointParams([]);
                        }
                    }
                }]);

//    $.each(self.content.endPoints(), function(i, data){
//        self.content.endPoints()[i].endPointParams = ko.observableArray(self.content.endPoints()[i].endPointParams);
//
//        self.content.endPoints()[i].changeContent = function(el) {
//            console.log(el);
//            self.content.endPoints()[i].type = el.type;
//            self.content.endPoints()[i].publishAlways = false;
//
//            if(el.type == 'MAIL') {
//                self.content.endPoints()[i].endPointParams([{"name":"to", value:""}]);
//            } else {
//                self.content.endPoints()[i].endPointParams([]);
//            }
//            console.log(self.content.endPoints());
//        }
//
//    });
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
                        self.content.endPoints()[i].publishAlways = false;

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

    self.content.schedule = ko.observable({
                startDate: ko.observable(""),
                endDate: ko.observable(""),
                interval: ko.observable(""),
                dates: ko.observable(null),
                days: ko.observable(null),
                times: ko.observable(null),
                startDateFormat: function(selected) {
                    var newDate = new Date(selected);
                    console.log("here");
                    var formattedDate = $.datepicker.formatDate("yy-mm-dd", new Date(newDate.setDate(newDate.getDate() + 1)));
                    $("#endDate").datepicker("option","minDate", formattedDate);
                    self.content.schedule().startDate(selected);

                },
                endDateFormat: function(selected) {
                    var newDate = new Date(selected);
                    console.log("and here");
                    var formattedDate = $.datepicker.formatDate("yy-mm-dd", new Date(newDate.setDate(newDate.getDate() - 1)));
                    $("#startDate").datepicker("option","maxDate", formattedDate);
                    self.content.schedule().endDate(selected);
                }
            });

    self.content.save = function(){
        var jsonDataOfModel = ko.toJSON(self.jsonData);
        console.log("JSON used for Create Rule", jsonDataOfModel);
        createRule(self.content.name(), jsonDataOfModel);
    };

    console.log(self);
}

