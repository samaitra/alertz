package flipkart.alert.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 8/1/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSeries extends BaseEntity{
    private Long seriesId;
    @NotEmpty
    private String name, source, query;

    @JsonIgnore
    private ScheduledRule rule;

    @JsonIgnore
    public Long getSeriesId() {
        return seriesId;
    }

    public DataSeries setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataSeries setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public DataSeries setSource(String source) {
        this.source = source;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public DataSeries setQuery(String query) {
        this.query = query;
        return this;
    }

    public ScheduledRule getRule() {
        return rule;
    }

    public DataSeries setRule(ScheduledRule rule) {
        this.rule = rule;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSeries)) return false;

        DataSeries that = (DataSeries) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
//        if (seriesId != null ? !seriesId.equals(that.seriesId) : that.seriesId != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;

        return true;
    }
}
