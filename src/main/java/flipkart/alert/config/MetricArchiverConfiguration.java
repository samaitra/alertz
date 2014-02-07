package flipkart.alert.config;

import java.util.Map;

public class MetricArchiverConfiguration {
    private boolean enabled;
    private String metricArchiverClass;
    private Map<String, Object> params;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMetricArchiverClass() {
        return metricArchiverClass;
    }

    public void setMetricArchiverClass(String metricArchiverClass) {
        this.metricArchiverClass = metricArchiverClass;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
