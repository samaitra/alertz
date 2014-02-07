/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 27/07/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */

var createRuleUrl = '/fk-alert-service/onDemandRules';
var getTeamsUrl = '/fk-alert-service/teams/onDemandRules';

$(document).ready(function () {
    $("#teams").typeahead({source: getTeams()});
    $('#headerPlaceHolder').load("/header.html");
    initDatePicker();
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

    self.endPointTypes = ["MAIL","NAGIOS"];

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

    self.content.save = function(){
        var jsonDataOfModel = ko.toJSON(self.jsonData);
        console.log("JSON used for Create Rule", jsonDataOfModel);
        createRule(self.content.name(), jsonDataOfModel);
    };

    console.log(self);
}

