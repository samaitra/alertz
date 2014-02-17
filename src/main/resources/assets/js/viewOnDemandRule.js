/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 29/07/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
var getRuleByNameUrl = '/alertz/onDemandRules/$ruleName';
var getRuleStatUrl ='/alertz/onDemandRules/$ruleId/latestStats';
var updateRuleUiLink = '/updateOnDemandRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/alertz/onDemandRules/$ruleId';
var viewRulesUrl = '/viewOnDemandRules.html?teamName=$teamName';

$(document).ready(function () {
    $('#headerPlaceHolder').load("/header.html");

    var ruleName = getParam("ruleName");
    initDateTimePicker();
    ko.applyBindings(new ViewOnDemandRuleModel(ruleName));
});

function ViewOnDemandRuleModel(ruleName) {
    this.rule = getRuleForKO(ruleName);
}




