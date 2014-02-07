package flipkart.alert.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 26/10/12
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */

@Setter @Getter
public class Metric implements Serializable{

    @NotEmpty
    private String key;
    private Object value;
    private Set<MetricTag> metricTags;
    private String metricSource = "OPENTSDB";
    private Date creationTime = new Date();
    public static String BREACHED = "breached";

    public Metric() {
        metricTags = new HashSet<MetricTag>();
    }

    public Metric(String key, Object value) {
        this();
        this.key = key;
        this.value = value;
    }

    public void addTag(MetricTag metricTag) {
        this.metricTags.add(metricTag);
    }

    public void addTags(Set<MetricTag> metricTags) {
        this.metricTags.addAll(metricTags);
    }

    public String toString() {
        return "Metric: "+this.key+" = "+value;
        //+" with metricTags "+ metricTags;
    }

    public String prepareTsdbTagsAsString() {
        StringBuilder tagString= new StringBuilder();
        for(MetricTag tag: metricTags) {
            tagString.append(tag.getTag()).append("=").append(tag.getValue())
                    .append(" ");
        }
        return tagString.toString();
    }

    private Metric(MetricBuilder builder) {
        this.key = builder.key;
        this.value = builder.value;
        this.metricTags = builder.metricTags;
    }

    public static class MetricBuilder {
        private String key;
        private Object value;
        private Set<MetricTag> metricTags;

        public MetricBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public MetricBuilder withValue(Object value) {
            this.value = value;
            return this;
        }

        public MetricBuilder withMetricTags(Set<MetricTag> metricTags) {
            this.metricTags = metricTags;
            return this;
        }

        public Metric build() {
            return new Metric(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        if (key != null ? !key.equals(metric.key) : metric.key != null) return false;
        if (metricSource != null ? !metricSource.equals(metric.metricSource) : metric.metricSource != null)
            return false;
//        if (metricTags != null ? !metricTags.equals(metric.metricTags) : metric.metricTags != null) return false;
        if (value != null ? !value.toString().equals(metric.value.toString()) : metric.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
//        result = 31 * result + (metricTags != null ? metricTags.hashCode() : 0);
        result = 31 * result + (metricSource != null ? metricSource.hashCode() : 0);
        return result;
    }
}
