package com.flipkart.alert.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 21/03/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlertVariable extends BaseEntity{
    private Long variableId;

    @NotEmpty
    private String name, value;

    @JsonIgnore
    private Rule rule;

    @JsonIgnore
    public Long getVariableId() {
        return variableId;
    }

    public AlertVariable setVariableId(Long variableId) {
        this.variableId = variableId;
        return this;
    }

    public String getName() {
        return name;
    }

    public AlertVariable setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public AlertVariable setValue(String value) {
        this.value = value;
        return this;
    }

    public Rule getRule() {
        return rule;
    }

    public AlertVariable setRule(Rule rule) {
        this.rule = rule;
        return this;
    }


}
