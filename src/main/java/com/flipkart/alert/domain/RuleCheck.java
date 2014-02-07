package com.flipkart.alert.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 8/1/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */

public class RuleCheck extends BaseEntity {
    private Long checkId;

    private String description;

    @NotEmpty
    private String booleanExpression, alertLevel;

    @JsonIgnore
    private Rule rule;

    @JsonIgnore
    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getDescription() {
        return description;
    }

    public RuleCheck setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getBooleanExpression() {
        return booleanExpression;
    }

    public RuleCheck setBooleanExpression(String booleanExpression) {
        this.booleanExpression = booleanExpression;
        return this;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public RuleCheck setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
        return this;
    }

    public Rule getRule() {
        return rule;
    }

    public RuleCheck setRule(Rule rule) {
        this.rule = rule;
        return this;
    }

    public String toString(){
        return " Check ID: " + checkId + " Description: " + description + " Expression: " + booleanExpression + " Alert Level: " + alertLevel + " ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleCheck)) return false;

        RuleCheck that = (RuleCheck) o;

        if (alertLevel != null ? !alertLevel.equals(that.alertLevel) : that.alertLevel != null) return false;
        if (booleanExpression != null ? !booleanExpression.equals(that.booleanExpression) : that.booleanExpression != null)
            return false;
//        if (checkId != null ? !checkId.equals(that.checkId) : that.checkId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;

        return true;
    }

}
