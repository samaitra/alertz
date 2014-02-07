package com.flipkart.alert.domain;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 6/12/12
 * Time: 3:31 PM
 * Metric Source Information
 */

import org.hibernate.Session;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetricSource extends BaseEntity{

    private Integer sourceId;

    @NotEmpty
    private String name, sourceType;

    @NotEmpty
    private Set<SourceConnectionParam> sourceConnectionParams;

    public Map<String,String> paramsToMap() {
        Map<String,String> paramsMap = new HashMap<String, String>();
        for(SourceConnectionParam param : sourceConnectionParams)
            paramsMap.put(param.getParam(), param.getValue());
        return paramsMap;
    }

    public void update(MetricSource withMetricSource) {
        Session session = beginTransaction();
        this.deleteParams();
        withMetricSource.setSourceId(this.sourceId);
        withMetricSource.update();
        commitTransaction(session);
    }

    private void deleteParams() {
        for(SourceConnectionParam param : this.sourceConnectionParams)
            param.delete();

    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Set<SourceConnectionParam> getSourceConnectionParams() {
        return sourceConnectionParams;
    }

    public void setSourceConnectionParams(Set<SourceConnectionParam> sourceConnectionParams) {
        this.sourceConnectionParams = sourceConnectionParams;
    }
}
