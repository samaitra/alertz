package com.flipkart.alert.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User: nitinka
 * Date: 16/1/13
 * Time: 12:54 PM
 */

public class SourceConnectionParam extends BaseEntity{
    @JsonIgnore
    private Integer paramId;

    @NotEmpty
    private String param, value;

    @JsonIgnore
    private MetricSource source;

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MetricSource getSource() {
        return source;
    }

    public void setSource(MetricSource source) {
        this.source = source;
    }
}
