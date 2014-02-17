/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 29/07/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
var getRuleByNameUrl = '/alertz/scheduledRules/$ruleName';
var getRuleStatUrl ='/alertz/scheduledRules/$ruleId/latestStats';
var updateRuleUiLink = '/updateScheduledRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/alertz/scheduledRules/$ruleId';
var viewRulesUrl = '/viewScheduledRules.html?teamName=$teamName';

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");

    var ruleName = getParam("ruleName");
    initDateTimePicker();
    ko.applyBindings(new ViewScheduleRuleModel(ruleName));
});

function ViewScheduleRuleModel(ruleName) {
    this.rule = getRuleForKO(ruleName);
    console.log(this.rule);
    var oldSchedule = this.rule.schedule;
    var newSchedule = {};

    newSchedule.startDate = $.datepicker.formatDate("dd-mm-yy", new Date(oldSchedule.startDate));
    newSchedule.endDate = $.datepicker.formatDate("dd-mm-yy", new Date(oldSchedule.endDate));
    newSchedule.interval = "Every " + oldSchedule.interval;
    newSchedule.dates = (oldSchedule.dates == null) ? "Not Configured" : oldSchedule.dates;
    newSchedule.times = (oldSchedule.times == null) ? "Not Configured" : oldSchedule.times;
    newSchedule.days = (oldSchedule.days == null) ? "Not Configured" : oldSchedule.days;

    this.rule.schedule = newSchedule;
}




