/**
 * Created by IntelliJ IDEA.
 * User: shashankg
 * Date: 12/11/13
 * Time: 5:35 PM
 * To change self template use File | Settings | File Templates.
 */

var getRuleByNameUrl = '/fk-alert-service/scheduledRules/$ruleName';
var getRuleStatUrl = '/fk-alert-service/scheduledRules/$ruleId/latestStats';
var updateRuleUiLink = '/updateScheduledRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/fk-alert-service/scheduledRules/$ruleId';
var viewRulesUrl = '/viewScheduledRules.html?teamName=$teamName';
var getTeamsUrl = '/fk-alert-service/teams/scheduledRules';
var getRulesForTeamUrl = '/fk-alert-service/teams/$teamName/scheduledRules';
var breachThreshold = 10;

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");
    initDateTimePicker();
    initPopOver();
    ko.applyBindings(new ViewScheduleRuleModel());

    expandAll = function() {
        $('.accordion-body').collapse('show')
    };

    collapseAll = function() {
        $('.accordion-body').collapse('hide');
    }
});

function ViewScheduleRuleModel() {
    var self = this;
    var today = new Date();
    var yesterday = new Date(today.setDate(today.getDate() - 1));
    self.breachDangerBar = ko.observable();
    self.breachWarningBar = ko.observable();
    self.breachInfoBar = ko.observable();
    self.breachSuccessBar = ko.observable();

    self.fromTime = $.datepicker.formatDate('yy/mm/dd', yesterday) + "-00:00:00";
    self.toTime = $.datepicker.formatDate('yy/mm/dd', new Date()) + "-00:00:00";
    self.allSchGraphs = ko.observableArray();
    self.dangerSchGraphs = ko.observableArray();
    self.warningSchGraphs = ko.observableArray();
    self.infoSchGraphs = ko.observableArray();
    self.successSchGraphs = ko.observableArray();
    self.selectedTeam = ko.observableArray()();
    self.isLoading = ko.observable(false);
    self.allTeams = getTeams();

    self.loadMetric = function() {
        self.fromTime = $.datepicker.formatDate('yy/mm/dd', yesterday) + "-00:00:00";
        self.toTime = $.datepicker.formatDate('yy/mm/dd', new Date()) + "-00:00:00";
        var breachDanger = 0;
        var breachWarning = 0;
        var breachInfo = 0;
        var breachSuccess = 0;
        self.isLoading(true);
        self.totalBreaches = 0;
        self.dangerSchGraphs.removeAll();
        self.warningSchGraphs.removeAll();
        self.infoSchGraphs.removeAll();
        self.successSchGraphs.removeAll();
        self.allSchGraphs.removeAll();

        var allRules = [];
        allRules = getRulesForTeam(self.selectedTeam);

        $.each(allRules, function(key, value) {
            var ruleName = value.name;
            var asciiResp = getAsciiValueForBreachFromTsdb(ruleName, self.fromTime, self.toTime);
            self.totalBreaches = getSumOfKeyFromAscii(asciiResp);
            self.rule = getRuleForKO(ruleName);
            var oldSchedule = self.rule.schedule;
            var newSchedule = {};
            self.breachRange = "";
            self.percentile = "";
            self.labelClass = getLabelClass(self.totalBreaches, self.fromTime, self.toTime);
            var responseTimeThreshold = getResponseTimeThreshold(value.ruleId);

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
                    self.breachRange = "[Response Time: Breach range (" + getBreachMinRange(resp, responseTimeThreshold, breachMax) + " ms, " + breachMax + " ms), ";
                    self.percentile = " 80% of breaches greater than: " + getPercentileForResponseTime(resp, responseTimeThreshold) + " ms]";
                }
            });

            newSchedule.startDate = $.datepicker.formatDate("dd-mm-yy", new Date(oldSchedule.startDate));
            newSchedule.endDate = $.datepicker.formatDate("dd-mm-yy", new Date(oldSchedule.endDate));
            newSchedule.interval = "Every " + oldSchedule.interval;
            newSchedule.dates = (oldSchedule.dates == null) ? "Not Configured" : oldSchedule.dates;
            newSchedule.times = (oldSchedule.times == null) ? "Not Configured" : oldSchedule.times;
            newSchedule.days = (oldSchedule.days == null) ? "Not Configured" : oldSchedule.days;

            self.rule.schedule = newSchedule;
            var item = {nameOfSchRule: ruleName, ruleDetails:self.rule,
                totalBreachCount : ko.observable(self.totalBreaches), breachRangeStr : ko.observable(self.breachRange),
                percentileStr: ko.observable(self.percentile), labelClass: ko.observable(self.labelClass)};
            if (self.rule.dataArchival.enabled)
                self.allSchGraphs.push(item);
        });

        $.each(self.allSchGraphs(), function(k, v) {
            if (v.labelClass() == "label label-important")
                self.dangerSchGraphs.push(v);
            else if (v.labelClass() == "label label-warning")
                self.warningSchGraphs.push(v);
            else if (v.labelClass() == "label label-info")
                self.infoSchGraphs.push(v);
            else
                self.successSchGraphs.push(v);
        });

        if (self.allSchGraphs().length > 0) {
            self.breachDangerBar((breachDanger * 100 ) / self.allSchGraphs().length);
            self.breachWarningBar((breachWarning * 100) / self.allSchGraphs().length);
            self.breachInfoBar((breachInfo * 100) / self.allSchGraphs().length);
            self.breachSuccessBar((breachSuccess * 100) / self.allSchGraphs().length);
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

        for (var index = 0; index < self.allSchGraphs().length; index++) {
            var chkStatLen = self.allSchGraphs()[index].ruleDetails.ruleStat.checkStats().length;

            for (var i = 0; i < chkStatLen; i ++) {
                self.allSchGraphs()[index].ruleDetails.ruleStat.checkStats()[i].fromTimeInputValue = self.fromTime;
                self.allSchGraphs()[index].ruleDetails.ruleStat.checkStats()[i].toTimeInputValue = self.toTime;
                if (self.allSchGraphs()[index].ruleDetails.ruleStat.checkStats()[i].variableUsed != null)
                    self.allSchGraphs()[index].ruleDetails.ruleStat.checkStats()[i].updateImg();
            }
            var asciiResp = getAsciiValueForBreachFromTsdb(self.allSchGraphs()[index].nameOfSchRule, self.fromTime, self.toTime);
            var newBreachCount = getSumOfKeyFromAscii(asciiResp);
            self.allSchGraphs()[index].totalBreachCount(newBreachCount);

            var responseTimeThreshold = getResponseTimeThreshold(self.allSchGraphs()[index].ruleDetails["ruleId"]);
            labelClass = getLabelClass(newBreachCount, self.fromTime, self.toTime);
            self.allSchGraphs()[index].labelClass(labelClass);

            $.each(self.allSchGraphs()[index].ruleDetails["variables"], function(key, content) {
                var resp = getAsciiValueForCheckFromTSDB(self.allSchGraphs()[index].nameOfSchRule, self.fromTime, self.toTime, content);
                if (content["name"] == "responseTime" && newBreachCount > 0) {
                    var breachMax = getMaxOfKeyFromAscii(resp);
                    self.allSchGraphs()[index].breachRangeStr("[Response Time: Breach range (" + getBreachMinRange(resp, responseTimeThreshold, breachMax) + " ms, " + breachMax + " ms), ");
                    self.allSchGraphs()[index].percentileStr("80% of breaches greater than: " + getPercentileForResponseTime(resp, responseTimeThreshold) + " ms]");
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

        if (self.allSchGraphs().length > 0) {
            self.breachDangerBar((breachDanger * 100 ) / self.allSchGraphs().length);
            self.breachWarningBar((breachWarning * 100) / self.allSchGraphs().length);
            self.breachInfoBar((breachInfo * 100) / self.allSchGraphs().length);
            self.breachSuccessBar((breachSuccess * 100) / self.allSchGraphs().length);
        }

        self.dangerSchGraphs.removeAll();
        self.warningSchGraphs.removeAll();
        self.infoSchGraphs.removeAll();
        self.successSchGraphs.removeAll();

        $.each(self.allSchGraphs(), function(k, v) {
            if (v.labelClass() == "label label-important")
                self.dangerSchGraphs.push(v);
            else if (v.labelClass() == "label label-warning")
                self.warningSchGraphs.push(v);
            else if (v.labelClass() == "label label-info")
                self.infoSchGraphs.push(v);
            else
                self.successSchGraphs.push(v);
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
        for (var index = 2; index < splitArr.length; index = index + 3)
            sum += parseInt(splitArr[index]);
    }
    return sum;
}

function getKeyArrayFromAscii(asciiValue) {
    var arr = [];
    if (asciiValue != undefined) {
        var splitArr = asciiValue.replace("\n", "").split(" ");
        for (var index = 2; index < splitArr.length; index = index + 3)
            arr.push(parseInt(splitArr[index]));
    }
    return arr;
}
function getMaxOfKeyFromAscii(asciiValue) {
    var arr = getKeyArrayFromAscii(asciiValue);
    return getMaxOfArray(arr);
}






