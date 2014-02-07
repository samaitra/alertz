/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 06/08/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */

var getRuleByNameUrl = '/fk-alert-service/onDemandRules/$ruleName';
var updateRuleUrl = '/fk-alert-service/onDemandRules/$ruleId';

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");

    var ruleName = getParam("ruleName");
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
//    Hacky code begins here
    $.each(self.content.endPoints(), function(i, data){
        self.content.endPoints()[i].endPointParams = ko.observableArray(self.content.endPoints()[i].endPointParams);
        self.content.endPoints()[i].publishAlways = ko.observable(self.content.endPoints()[i].publishAlways.toString());

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
//    Hacky code ends here
    self.content.removeEndPoints = function() {
        self.content.endPoints.pop();
    };

//    self.content.newVariables = ko.observableArray();
//    $.each(self.content.variables(), function(i, data){
//        self.content.newVariables.push({name: "$"+data.name});
//    });
//
//    self.content.updateDropDown = function(){
//        self.content.newVariables.removeAll();
//        $.each(self.content.variables(), function(i, data){
//            self.content.newVariables.push({name: "$"+data.name});
//        });
//        return true;
//    };


    self.content.save = function(){
        var jsonDataOfModel = ko.toJSON(self.jsonData);
        console.log("JSON used for Update Rule", jsonDataOfModel);
        updateRule(self.content.ruleId, self.content.name(), jsonDataOfModel);
    };

    console.log(self);

}