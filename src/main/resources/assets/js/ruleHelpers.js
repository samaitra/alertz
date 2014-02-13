/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 26/07/13
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */

function getAllRules() {
    var url = getAllRulesUrl;
    var response = [];
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch Rules");
        }

    });
    return response;
}

function getOnDemandRulesForTeam(teamName) {
    var url = getOnDemandTeamsUrl.replace("$teamName", teamName);
    var response = [];
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch Rules for Team" + teamName + "!");
        }

    });
    return response;
}


function getRuleByName(ruleName) {
    var url = getRuleByNameUrl.replace("$ruleName", ruleName);
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "No Rules found matching the search criteria");
        }

    });
    return response;
}

function getRulesForTeam(teamName) {
    var url = getRulesForTeamUrl.replace("$teamName", teamName);
    var response = [];
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch Rules");
        }

    });
    return response;
}

function getTeams() {
    var url = getTeamsUrl;
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch teams for rules!")
        }
    });
    return response;
}

function getTeamsForOnDemandRule() {
    var url = getTeamsForOnDemandRuleUrl;
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch teams for rules!")
        }
    });
    console.log(response);
    return response;
}

function getRuleNames(startsWith) {
    var url = getRuleNamesUrl.replace('$startsWith', startsWith);
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch Rule Names!")
        }
    });
    return response;
}

function getRuleStat(ruleId) {
    var url = getRuleStatUrl.replace("$ruleId", ruleId);
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            response = (err.status == 404) ? null : defineErrorType(err, "Something went wrong!");
        }
    });

    return response;
}

function createRule(ruleName, jsonDataOfModel) {
    var url = createRuleUrl;
    $.ajax({
        type: 'POST',
        url: url,
        data: jsonDataOfModel,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (replyJson) {
            $('#message').removeClass().addClass("alert alert-success").text("Rule " + ruleName + " has been created with ID: " + replyJson["ruleId"]);

        },
        error: function () {
            $('#message').removeClass().addClass("alert alert-error").text("Failed to Create Rule: " + ruleName);
        }
    });
}

function updateRule(ruleId, ruleName, jsonDataOfModel) {
    var url = updateRuleUrl.replace("$ruleId", ruleId);
    $.ajax({
        type: 'PUT',
        url: url,
        data: jsonDataOfModel,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (replyJson) {
            $('#message').addClass("alert alert-success").text("Rule " + ruleName + " has been updated");

        },
        error: function () {
            $('#message').addClass("alert alert-error").text("Failed to Update Rule: " + ruleName);
        }
    });
}

function deleteRule(ruleId) {
    var url = deleteRuleUrl.replace("$ruleId", ruleId);
    $.ajax({
        type: 'DELETE',
        url: url,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (replyJson) {
            $('#message').addClass("alert alert-success").text("Rule Deleted Successfully!");
        },
        error: function () {
            $('#message').addClass("alert alert-error").text("An error occurred!");
        }
    });
}

function runRule(ruleId) {
    var url = "/fk-alert-service/scheduledRules/" + ruleId + "/forceRun";
    $.ajax({
        type: 'POST',
        url: url,
        async: true,
        cache: false,
        contentType: 'application/json',
        success: function (replyJson) {
            $('#message').addClass("alert alert-success").text("Rule ID: " + ruleId + " Run Successfully!");
            window.setTimeout(function() {
                location.reload()
            }, 3000);
        },
        error: function () {
            $('#message').addClass("alert alert-error").text("An error occurred!");
        }
    });
}

function getRuleStatus(ruleId) {
    var url = '/fk-alert-service/scheduledRules/' + ruleId + "/status";
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch Rule Status!")
        }
    });
    return response;
}

function OnDemandRulesViewModel(rules, isTeamBased) {
    var self = this;
    self.rules = []
    if (rules.length > 0) {
        $('#message').addClass("alert alert-success").text("Fetched Rules Successfully!");
        $('#rulesTable').addClass("container-fluid show");

        $.each(rules, function(index, rule) {
            self.rules.push(fetchRuleTemplate(rule, isTeamBased));
        });
    } else {
        $('#message').addClass("alert alert-warning").text("No Rules found matching the search criteria");
    }
}

function ScheduledRulesViewModel(rules, isTeamBased) {
    var self = this;
    self.pauseAllClass = ko.observable("icon-pause");
    self.showPlayAll = ko.observable(false);
    self.showPauseAll = ko.observable(false);
    self.rules = [];
    var teamName = getParam("teamName");
    var teams = new Array();
    (teamName == "all") ? teams = getTeams() : teams.push(teamName);

    self.shouldShowPlayAndPauseAll = function() {
        var statusArray = new Array();
        self.showPauseAll(false);
        self.showPlayAll(false);

        $.each(teams, function(key, t) {
            var allJobDetails = new Array();
            allJobDetails = getAllJobDetailsForTeam(t);
            var index = allJobDetails.length;
            while (index--)
                statusArray.push(allJobDetails[index].status);
        });

        if (statusArray.contains("NORMAL"))
            self.showPauseAll(true);
        if (statusArray.contains("PAUSED"))
            self.showPlayAll(true);
    };

    self.shouldShowPlayAndPauseAll();

    self.playAll = function(rule) {
        var msg = "This action will resume all the rules. It might take a while. Do you want to proceed?";
        bootbox.confirm(msg, function(result) {
            if (result) {
                $.each(teams, function(key, t) {
                    var allJobDetails = new Array();
                    var statusArray = new Array();
                    allJobDetails = getAllJobDetailsForTeam(t);
                    var index = allJobDetails.length;
                    while (index--)
                        statusArray.push(allJobDetails[index].status);

                    if (statusArray.contains("PAUSED")) {
                        $('#message').addClass("alert alert-info").text("Resuming rules for " + t + "...");
                        resumeAllTeamRule(t);
                    }
                    else
                        $('#message').addClass("alert alert-info").text("All rules are already running for " + t + " !");
                });

                $('#message').addClass("alert alert-info").text("Successfully resumed all the rules.");
                window.setTimeout(function() {
                    location.reload()
                }, 1000);
            }
        });
    };

    self.pauseAll = function(rule) {
        var msg = "This action will pause all the rules. It might take a while. Do you want to proceed?";
        bootbox.confirm(msg, function(result) {
            if (result) {
                $.each(teams, function(key, t) {
                    var allJobDetails = new Array();
                    var statusArray = new Array();
                    allJobDetails = getAllJobDetailsForTeam(t);
                    var index = allJobDetails.length;
                    while (index--)
                        statusArray.push(allJobDetails[index].status);

                    if (statusArray.contains("NORMAL")) {
                        $('#message').addClass("alert alert-info").text("Resuming rules for " + t + "...");
                        pauseAllTeamRule(t);
                    }
                    else
                        $('#message').addClass("alert alert-info").text("All rules are already paused for " + t + " !");
                });

                $('#message').addClass("alert alert-info").text("Successfully resumed all the rules.");

                window.setTimeout(function() {
                    location.reload()
                }, 1000);
            }
        });
    };

    if (rules.length > 0) {
//        $('#message').addClass("alert alert-success").text("Fetched Rules Successfully!");
        $('#rulesTable').addClass("container-fluid show");
        $.each(rules, function(index, data) {
            var rule = fetchRuleTemplate(data, isTeamBased);
            rule.btnIcon = ko.observable();
            var ruleStatus = getRuleStatus(data["ruleId"]);

            var status = ruleStatus.status;
            if (status == "NORMAL")
                rule.btnIcon("icon-pause");
            else if (status == "PAUSED")
                rule.btnIcon("icon-play-circle");
            else
                rule.btnIcon("icon-warning-sign");

            rule.nextFireTime = "Not Available";
            if (ruleStatus["nextFireTime"])
                rule.nextFireTime = new Date(ruleStatus["nextFireTime"].replace("IST", "")).toLocaleString();
            self.rules.push(rule);

            rule.togglePause = function (rule) {
                var status = getRuleStatus(rule.ruleId).status;
                if (status == "NORMAL") {
                    pauseRule(rule.ruleId);
                    rule.btnIcon("icon-play-circle");
                }
                else if (status == "PAUSED") {
                    resumeRule(rule.ruleId);
                    rule.btnIcon("icon-pause");
                }
                else {
                    rule.btnIcon("icon-warning-sign");
                    return;
                }
                self.shouldShowPlayAndPauseAll();
            };
        });
    } else {
        $('#message').addClass("alert alert-warning").text("No Rules found matching the search criteria");
    }
}

function fetchRuleTemplate(data, isTeamBased) {
    var rule = {};
    rule.ruleId = data["ruleId"];
    rule.name = data["name"];
    rule.ruleLink = getRuleUiLink.replace("$ruleName", data["name"]);
    rule.updateRuleLink = updateRuleUiLink.replace("$ruleName", data["name"]);
    console.log(rule.updateRuleLink);
    rule.ruleStat = getRuleStat(data["ruleId"]);
    rule.lastCheckTime = null;
    rule.breached = null;
    rule.statusImg = null;

    if (rule.ruleStat != null) {
        rule.lastCheckTime = new Date(rule.ruleStat["lastCheckTime"]).toLocaleString();
        rule.breached = rule.ruleStat["breached"];
        rule.statusImg = rule.breached ? "image/failure.png" : "image/success.png";
    }

    rule.deleteAction = function(el) {
        var msg = "Clicking OK will delete the rule: " + el.name + ". Are you sure you want to proceed?";
        bootbox.confirm(msg, function(result) {
            if (result) {
                deleteRule(el.ruleId);
                window.setTimeout(function() {
                    location.reload()
                }, 3000);
            }
        });
    };

    rule.runNowAction = function(el) {
        runRule(el.ruleId);
    };

    if (isTeamBased) {
        rule.team = null;
        $('#teamHeader').addClass("hide");
    } else {
        $('#teamHeader').addClass("show");
        rule.team = data["team"];
    }
    return rule;
}

function getRuleForKO(ruleName) {
    this.rule = getRuleByName(ruleName);
    var rule = this.rule;
    var ruleStat = getRuleStat(this.rule["ruleId"]);
    rule.dataArchival = getDataArchivalSource();
    if (ruleStat == null) {
        $('#checkStatusContainer').hide();
        $('#ruleStatusContainer').hide();
    }
    if (ruleStat != null) {
        var ruleStatus = ruleStat["breached"] ? "Failure " : "Success ";
        var lastCheckTime = " (Checked at: " + (new Date(ruleStat["lastCheckTime"]).toLocaleString() || null) + ")";
        ruleStat.ruleStatus = ruleStatus + lastCheckTime;
        ruleStat.statusClass = ruleStat["breached"] ? "label label-important" : "label label-success";


        var checkStats = [];

        $.each(ruleStat.checkStats, function(index, statusData) {
            var checkStat = new Object();

            checkStat.checkStatus = statusData.breached ? "Failure " : "Success ";
            checkStat.statusClass = statusData.breached ? "label label-important" : "label label-success";
            checkStat.description = statusData.description;
            checkStat.expression = statusData.expression;
            checkStat.variableUsed = null;

            if (rule.dataArchival.enabled) {
                var now = new Date();
                $.each(rule["variables"], function(index, content) {
                    if (checkStat.expression.indexOf(content["name"]) > -1) {
                        checkStat.variableUsed = content;
                        var yesterday = new Date();
                        yesterday.setDate(yesterday.getDate()-1);

                        checkStat.fromTimeInputValue = $.datepicker.formatDate('yy/mm/dd', yesterday) + "-00:00:00";
                        checkStat.toTimeInputValue = $.datepicker.formatDate('yy/mm/dd', now) + "-"+now.getHours()+":"+now.getMinutes()+":"+now.getSeconds();

                        checkStat.updateGraph = checkStat.variableUsed.name + "_updateGraph";

                        var variableKeyName = ruleName + "." + content["name"]
                        if(content["value"].indexOf("${") > -1) {
                            variableKeyName += "{variableKey=*}";
                        }

                        checkStat.updateImg = function() {
                            checkStat.img("/fk-alert-service/archivedMetrics/rules/" + rule["ruleId"] + "?start=" + checkStat.fromTimeInputValue + "&end=" + checkStat.toTimeInputValue + "&o=&wxh=600x300&png");
                            checkStat.breachedImg("/fk-alert-service/archivedMetrics/rules/" + rule["ruleId"] + "?start=" + checkStat.fromTimeInputValue + "&end=" + checkStat.toTimeInputValue + "&metricType=BREACH&o=&wxh=600x300&png");
                        }
                        checkStat.img = ko.observable("/fk-alert-service/archivedMetrics/rules/" + rule["ruleId"] + "?start=" + checkStat.fromTimeInputValue + "&end=" + checkStat.toTimeInputValue + "&o=&wxh=600x300&png");
                        checkStat.breachedImg = ko.observable("/fk-alert-service/archivedMetrics/rules/" + rule["ruleId"] + "?start=" + checkStat.fromTimeInputValue + "&end=" + checkStat.toTimeInputValue + "&metricType=BREACH&o=&wxh=600x300&png");
                    }
                });
            }
            checkStats.push(checkStat)

        });
        ruleStat.checkStats = ko.observableArray(checkStats);
    }
    rule.updateRuleLink = updateRuleUiLink.replace("$ruleName", rule.name);
    rule.deleteAction = function(el) {
        var msg = "Clicking OK will delete the rule: " + el.name + ". Are you sure you want to proceed?";
        bootbox.confirm(msg, function(result) {
            if (result) {
                deleteRule(el.ruleId);
                window.setTimeout(function() {
                    location.href = viewRulesUrl.replace("$teamName", rule.team)
                }, 3000);
            }
        });
    };

    rule.runNowAction = function(el) {
        runRule(el.ruleId);
    }
    this.rule.dataArchival = rule.dataArchival;
    this.rule.ruleStat = ruleStat;
    return this.rule;
}

function getDataArchivalSource() {
    var url = '/fk-alert-service/configurations/metricArchiverConfiguration';
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function(err) {
            defineErrorType(err, "Failed to fetch metric source!")
        }
    });
    return response;
}

function initDateTimePicker() {
    ko.bindingHandlers.datetimepicker = {
        init: function(element, valueAccessor, allBindingsAccessor) {
            var options = allBindingsAccessor().datetimepickerOptions || {
                dateFormat: 'yy/mm/dd',
                timeFormat: 'HH:mm:ss',
                separator: '-'
            };
            $(element).datetimepicker(options);

        },
        update: function(element, valueAccessor) {
        }
    };
}

function initDatePicker() {
    ko.bindingHandlers.datepicker = {
        init: function (element, valueAccessor, allBindingsAccessor) {
            console.log("in init");
            var options = allBindingsAccessor().datepickerOptions || {};
            $(element).datepicker(options);

            //handle the field changing
            ko.utils.registerEventHandler(element, "change", function () {
                var observable = valueAccessor();
                observable($(element).val());
                if (observable.isValid()) {
                    observable($(element).datepicker("getDate"));

                    $(element).blur();
                }
            });

            //handle disposal (if KO removes by the template binding)
            ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                $(element).datepicker("destroy");
            });

        },
        update: function (element, valueAccessor) {
            var value = ko.utils.unwrapObservable(valueAccessor());

            //handle date data coming via json from Microsoft
            if (String(value).indexOf('/Date(') == 0) {
                value = new Date(parseInt(value.replace(/\/Date\((.*?)\)\//gi, "$1")));
            }

            current = $(element).datepicker("getDate");

            if (value - current !== 0) {
                $(element).datepicker("setDate", value);
            }
        }
    };

}

function initPopOver() {
    ko.bindingHandlers.bootstrapPopover = {
        init: function(element, valueAccessor, allBindingsAccessor, viewModel) {
            var options = valueAccessor();
            var defaultOptions = {};
            options = $.extend(true, {}, defaultOptions, options);
            $(element).popover(options);
        }
    };
}

function pauseAllTeamRule(teamName) {
    var url = "/fk-alert-service/teams/" + teamName + "/pauseRules";
    var response;
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (jsonData) {
            response = jsonData;
            $('#message').addClass("alert alert-success").text("All rules paused successfully for " + teamName);
        },
        error: function () {
            $('#message').addClass("alert alert-error").text("An error occurred!");
        }
    });
    return response;
}

function resumeAllTeamRule(teamName) {
    var url = "/fk-alert-service/teams/" + teamName + "/resumeRules";
    var response;
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (jsonData) {
            response = jsonData;
            $('#message').addClass("alert alert-success").text("All rules resumed successfully for " + teamName);
        },
        error: function () {
            $('#message').addClass("alert alert-error").text("An error occurred!");
        }
    });
    return response;
}

function pauseRule(ruleId) {
    var url = '/fk-alert-service/scheduledRules/' + ruleId + '/pause';
    var response;
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
            $('#message').addClass("alert alert-success").text("Successfully paused the job!");
        },
        error: function(err) {
            defineErrorType(err, "Failed to pause the job!")
        }
    });
    return response;
}

function resumeRule(ruleId) {
    var url = '/fk-alert-service/scheduledRules/' + ruleId + '/resume';
    var response;
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        cache: false,
        contentType:'application/json',
        success: function (jsonData) {
            response = jsonData;
            $('#message').addClass("alert alert-success").text("Successfully resumed the job!");
        },
        error: function(err) {
            defineErrorType(err, "Failed to resume the job!")
        }
    });
    return response;
}

function getAllJobDetailsForTeam(teamName) {
    var url = "/fk-alert-service/teams/" + teamName + "/allJobDetails";
    var response;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        cache: false,
        contentType: 'application/json',
        success: function (jsonData) {
            response = jsonData;
        },
        error: function () {
            $('#message').addClass("alert alert-error").text("An error occurred!");
        }
    });
    return response;
}

Array.prototype.contains = function(obj) {
    var index = this.length;
    while (index--)
        if (this[index] === obj)
            return true;
    return false;
}