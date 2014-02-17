/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 26/07/13
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */


var getAllRulesUrl = '/alertz/onDemandRules';
var getRuleByNameUrl = '/alertz/onDemandRules/$ruleName';
var getRulesForTeamUrl = '/alertz/teams/$teamName/onDemandRules';
var getTeamsUrl = '/alertz/teams/onDemandRules';
var getRuleStatUrl ='/alertz/onDemandRules/$ruleId/latestStats';
var getRuleUiLink = '/viewOnDemandRule.html?ruleName=$ruleName';
var updateRuleUiLink = '/updateOnDemandRule.html?ruleName=$ruleName';
var deleteRuleUrl = '/alertz/onDemandRules/$ruleId';
var getRuleNamesUrl = '/alertz/onDemandRules/names?startsWith=$startsWith';

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
            ko.applyBindings(new OnDemandRulesViewModel(rules, false));
        } else {
            rules = getAllRules();
            ko.applyBindings(new OnDemandRulesViewModel(rules, false));
        }
    } else if(!teamName.isBlank()){
        rules = getRulesForTeam(teamName);
        ko.applyBindings(new OnDemandRulesViewModel(rules, true));
    }

    $('#ruleSearch').submit(function(e) {
        var ruleName = $('#ruleName').val();
        var teamName = $('#teams').val();
        location.href = '/viewOnDemandRules.html?ruleName=' + ruleName + "&teamName=" + teamName;
        return false;
    });
});