package flipkart.alert.domain;

import flipkart.alert.util.SetHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 26/10/12
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Setter @Getter @ToString
public class Alert {
    private Date alertDate;
    private List<Metric> metrics;
    private Rule rule;
    private List<RuleCheck> breachedChecks;

    public Alert(){}
    public Alert(List<Metric> metrics, Rule rule, List<RuleCheck> breachedChecks) {
        this.metrics = metrics;
        this.rule = rule;
        this.breachedChecks = breachedChecks;
        this.alertDate = new Date();
        resolveCheckDescriptionWithMetricValues();
    }

    /**
     * Would replace any metric variable defined in checkDescription
     */
    @JsonIgnore
    private void resolveCheckDescriptionWithMetricValues() {
        for (RuleCheck breachedCheck :breachedChecks) {
            String checkDescription = breachedCheck.getDescription();
            for(Metric metric : metrics) {
                checkDescription = checkDescription.replace("$"+metric.getKey(), metric.getValue().toString());
            }
            breachedCheck.setDescription(checkDescription);

        }
    }

}
