/**
 * Created by IntelliJ IDEA.
 * User: shashankg
 * Date: 12/11/13
 * Time: 5:35 PM
 * To change self template use File | Settings | File Templates.
 */

var getAllRulesUrl = '/alertz/onDemandRules';
var getRuleByNameUrl = '/alertz/onDemandRules/$ruleName';
var getRulesForTeamUrl = '/alertz/teams/$teamName/onDemandRules';
var getTeamsUrl = '/alertz/teams/onDemandRules';
var getRuleUiLink = '/viewOnDemandRule.html?ruleName=$ruleName';
var updateRuleUiLink = '/updateOnDemandRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/alertz/onDemandRules/$ruleId';
var getRuleNamesUrl = '/alertz/onDemandRules/names?startsWith=$startsWith';
var getRuleStatUrl = '/alertz/onDemandRules/$ruleId/latestStats';
var getTeamsForOnDemandRuleUrl = '/alertz/teams/onDemandRules'
var getOnDemandTeamsUrl = '/alertz/teams/$teamName/onDemandRules'
var breachThreshold = 10;

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");
    initDateTimePicker();
    initPopOver();
    ko.applyBindings(new ViewOnDemandRuleModel());

    expandAll = function() {
        $('.accordion-body').collapse('show')
    };

    collapseAll = function() {
        $('.accordion-body').collapse('hide');
    }
});


function ViewOnDemandRuleModel() {
    var self = this;
    var today = new Date();
    var yesterday = new Date(today.setDate(today.getDate() - 1));

    self.fromTime = $.datepicker.formatDate('yy/mm/dd', yesterday) + "-00:00:00";
    self.toTime = $.datepicker.formatDate('yy/mm/dd', new Date()) + "-00:00:00";
    self.allGraphs = ko.observableArray();
    self.dangerGraphs = ko.observableArray();
    self.warningGraphs = ko.observableArray();
    self.infoGraphs = ko.observableArray();
    self.successGraphs = ko.observableArray();
    self.totalBreaches = 0;
    self.breachDangerBar = ko.observable();
    self.breachWarningBar = ko.observable();
    self.breachInfoBar = ko.observable();
    self.breachSuccessBar = ko.observable();
    self.selectedTeam = ko.observableArray()();
    self.isLoading = ko.observable(false);
    self.allTeams = getTeams();

    self.loadMetric = function() {
        self.fromTime = $.datepicker.formatDate('yy/mm/dd', yesterday) + "-00:00:00";
        self.toTime = $.datepicker.formatDate('yy/mm/dd', new Date()) + "-00:00:00";
        self.totalBreaches = 0;
        self.isLoading(true);
        self.dangerGraphs.removeAll();
        self.warningGraphs.removeAll();
        self.infoGraphs.removeAll();
        self.successGraphs.removeAll();
        self.allGraphs.removeAll();
        var breachDanger = 0;
        var breachWarning = 0;
        var breachInfo = 0;
        var breachSuccess = 0;

        var allRules = [];
        allRules = getRulesForTeam(self.selectedTeam);
        $.each(allRules, function(key, value) {
            var ruleName = value.name;
            self.rule = getRuleForKO(ruleName);
            var asciiResp = getAsciiValueForBreachFromTsdb(ruleName, self.fromTime, self.toTime);
            self.totalBreaches = getSumOfKeyFromAscii(asciiResp);
            self.breachRange = "";
            self.percentile = "";
            var responseTimeThreshold;
            var statResp = getRuleStat(value.ruleId);
            self.labelClass = getLabelClass(self.totalBreaches, self.fromTime, self.toTime);
            $.each(statResp.checkStats, function(k, v) {
                if (v.expression.match("responseTime") != null) {
                    var arr = v.expression.split(">");
                    responseTimeThreshold = arr[1];
                }
            });

            if (self.labelClass == "label label-important")
                breachDanger++;
            else if (self.labelClass == "label label-warning")
                breachWarning++;
            else if (self.labelClass == "label label-info")
                breachInfo++;
            else
                breachSuccess++;

            $.each(self.rule["variables"], function(key, content) {
                var resp = getAsciiValueForCheckFromTSDB(ruleName, self.fromTime, self.toTime, content);
                if (content["name"] == "responseTime" && self.totalBreaches > 0) {
                    var breachMax = getMaxOfKeyFromAscii(resp);
                    self.breachRange = "[ Response Time: Breach range (" + getBreachMinRange(resp, responseTimeThreshold, breachMax) + " ms, " + breachMax + "), ";
                    self.percentile = " 80% of breaches greater than: " + getPercentileForResponseTime(resp, responseTimeThreshold) + " ms]";
                }
            });

            if (self.rule != undefined && self.rule.ruleStat != null) {
                var item = {nameOfRule: ruleName, ruleDetails:self.rule, totalBreachCount : ko.observable(self.totalBreaches),
                    breachRangeStr : ko.observable(self.breachRange), percentileStr : ko.observable(self.percentile),
                    labelClass: ko.observable(self.labelClass)};
            }
            if (self.rule.dataArchival.enabled)
                self.allGraphs.push(item);
        });


        $.each(self.allGraphs(), function(k, v) {
            if (v.labelClass() == "label label-important")
                self.dangerGraphs.push(v);
            else if (v.labelClass() == "label label-warning")
                self.warningGraphs.push(v);
            else if (v.labelClass() == "label label-info")
                self.infoGraphs.push(v);
            else
                self.successGraphs.push(v);
        });

        if (self.allGraphs().length > 0) {
            self.breachDangerBar((breachDanger * 100 ) / self.allGraphs().length);
            self.breachWarningBar((breachWarning * 100) / self.allGraphs().length);
            self.breachInfoBar((breachInfo * 100) / self.allGraphs().length);
            self.breachSuccessBar((breachSuccess * 100) / self.allGraphs().length);
        }
        self.isLoading(false);
        drawHighChart(self.breachSuccessBar(), self.breachInfoBar(), self.breachWarningBar(), self.breachDangerBar(),
            self.selectedTeam, "From: " + self.fromTime + ", To: " + self.toTime);
    };

    self.updateAllGraphs = function() {
        self.isLoading(true);
        var breachDanger = 0;
        var breachWarning = 0;
        var breachInfo = 0;
        var breachSuccess = 0;
        var labelClass;

        for (var index = 0; index < self.allGraphs().length; index++) {
            var chkStatLen = self.allGraphs()[index].ruleDetails.ruleStat.checkStats().length;
            for (var i = 0; i < chkStatLen; i ++) {
                self.allGraphs()[index].ruleDetails.ruleStat.checkStats()[i].fromTimeInputValue = self.fromTime;
                self.allGraphs()[index].ruleDetails.ruleStat.checkStats()[i].toTimeInputValue = self.toTime;
                if (self.allGraphs()[index].ruleDetails.ruleStat.checkStats()[i].variableUsed != null)
                    self.allGraphs()[index].ruleDetails.ruleStat.checkStats()[i].updateImg();
            }

            var nameRule = self.allGraphs()[index].nameOfRule;
            var asciiResp = getAsciiValueForBreachFromTsdb(nameRule, self.fromTime, self.toTime);
            var newBreachCount = getSumOfKeyFromAscii(asciiResp);
            self.allGraphs()[index].totalBreachCount(newBreachCount);

            var responseTimeThreshold;
            labelClass = getLabelClass(newBreachCount, self.fromTime, self.toTime);
            self.allGraphs()[index].labelClass(labelClass);
            var statResp = getRuleStat(self.allGraphs()[index].ruleDetails["ruleId"]);
            $.each(statResp.checkStats, function(k, v) {
                if (v.expression.match("responseTime") != null) {
                    var arr = v.expression.split(">");
                    responseTimeThreshold = arr[1];
                }
            });

            $.each(self.allGraphs()[index].ruleDetails["variables"], function(key, content) {
                var resp = getAsciiValueForCheckFromTSDB(nameRule, self.fromTime, self.toTime, content);
                if (content["name"] == "responseTime" && newBreachCount > 0) {
                    var breachMax = getMaxOfKeyFromAscii(resp);
                    self.allGraphs()[index].breachRangeStr("[ Response Time: Breach range (" + getBreachMinRange(resp, responseTimeThreshold, breachMax) + " ms, " + breachMax + " ms), ");
                    self.allGraphs()[index].percentileStr(" 80% of breaches greater than: " + getPercentileForResponseTime(resp, responseTimeThreshold) + " ms]");
                }
            });

            if (labelClass == "label label-important")
                breachDanger++;
            else if (labelClass == "label label-warning")
                breachWarning++;
            else if (labelClass == "label label-info")
                breachInfo++;
            else
                breachSuccess++
        }
        if (self.allGraphs().length > 0) {
            self.breachDangerBar((breachDanger * 100 ) / self.allGraphs().length);
            self.breachWarningBar((breachWarning * 100) / self.allGraphs().length);
            self.breachInfoBar((breachInfo * 100) / self.allGraphs().length);
            self.breachSuccessBar((breachSuccess * 100) / self.allGraphs().length);
        }

        self.dangerGraphs.removeAll();
        self.warningGraphs.removeAll();
        self.infoGraphs.removeAll();
        self.successGraphs.removeAll();

        $.each(self.allGraphs(), function(k, v) {
            if (v.labelClass() == "label label-important")
                self.dangerGraphs.push(v);
            else if (v.labelClass() == "label label-warning")
                self.warningGraphs.push(v);
            else if (v.labelClass() == "label label-info")
                self.infoGraphs.push(v);
            else
                self.successGraphs.push(v);
        });
        self.isLoading(false);
        drawHighChart(self.breachSuccessBar(), self.breachInfoBar(), self.breachWarningBar(), self.breachDangerBar(),
            self.selectedTeam, "From: " + self.fromTime + ", To: " + self.toTime);
    }
}

function getSumOfKeyFromAscii(asciiValue) {
    var sum = 0;
    if (asciiValue != undefined) {
        var splitArr = asciiValue.replace("\n", "").split(" ");
        for (var index = 2; index < splitArr.length; index = index + 6)
            sum += parseInt(splitArr[index]);
    }
    return sum;
}

function getKeyArrayFromAscii(asciiValue) {
    var arr = [];
    if (asciiValue != undefined) {
        var splitArr = asciiValue.replace("\n", "").split(" ");
        for (var index = 2; index < splitArr.length; index = index + 6)
            arr.push(parseInt(splitArr[index]));
    }
    return arr;
}

function getMaxOfKeyFromAscii(asciiValue) {
    var arr = getKeyArrayFromAscii(asciiValue);
    return getMaxOfArray(arr);
}








