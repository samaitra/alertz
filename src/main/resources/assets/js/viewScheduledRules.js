/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 25/07/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */

var getAllRulesUrl = '/alertz/scheduledRules';
var getRuleByNameUrl = '/alertz/scheduledRules/$ruleName';
var getRulesForTeamUrl = '/alertz/teams/$teamName/scheduledRules';
var getTeamsUrl = '/alertz/teams/scheduledRules';
var getRuleStatUrl ='/alertz/scheduledRules/$ruleId/latestStats';
var getRuleUiLink = '/viewScheduledRule.html?ruleName=$ruleName';
var updateRuleUiLink = '/updateScheduledRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/alertz/scheduledRules/$ruleId';
var getRuleNamesUrl = '/alertz/scheduledRules/names?startsWith=$startsWith';

$(document).ready(function () {
    $("#ruleName").typeahead({
                source: function(query, process) {
                    process(getRuleNames(query));
                },
                minLength: 3
            });
    $('#headerPlaceHolder').load("/header.html");
    var teams = getTeams();
    var list = $('#teams')[0];
    $.each(teams, function(index, text) {
        list.options[list.options.length] = new Option(text, text);
    });

    var ruleName = getParam("ruleName");
    var teamName = getParam("teamName");
    var rules = [];

    $('#ruleName').val(ruleName);
    $('#teams').val(teamName);

    if(teamName == "all") {
        rules = [];
        if(!ruleName.isBlank()) {
            var fetchedRule = getRuleByName(ruleName);
            if(fetchedRule == null)
                return;
            rules.push(fetchedRule);
            ko.applyBindings(new ScheduledRulesViewModel(rules, false));
        } else {
            rules = getAllRules();
            ko.applyBindings(new ScheduledRulesViewModel(rules, false));
        }
    } else if(!teamName.isBlank()){
        rules = getRulesForTeam(teamName);
        ko.applyBindings(new ScheduledRulesViewModel(rules, true));
    }

    $('#ruleSearch').submit(function(e) {
        var ruleName = $('#ruleName').val();
        var teamName = $('#teams').val();
        location.href = '/viewScheduledRules.html?ruleName=' + ruleName + "&teamName=" + teamName;
        return false;
    });
});




