package flipkart.alert.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndPoint extends BaseEntity{
    private Long endPointId;

    @NotEmpty
    private String type;

    private boolean publishAlways = false;

    @JsonIgnore
    private Rule rule;

    @Valid
    private Set<EndPointParam> endPointParams;

    @JsonIgnore
    public Long getEndPointId() {
        return endPointId;
    }

    public EndPoint setEndPointId(Long endPointId) {
        this.endPointId = endPointId;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setEndPointParams(Set<EndPointParam> endPointParams) {
        this.endPointParams = endPointParams;
    }

    public Set<EndPointParam> getEndPointParams() {
        return endPointParams;
    }

    public EndPoint setType(String type) {
        this.type = type;
        return this;
    }

    public Rule getRule() {
        return rule;
    }

    public EndPoint setRule(Rule rule) {
        this.rule = rule;
        return this;
    }

    public boolean isPublishAlways() {
        return publishAlways;
    }

    public void setPublishAlways(boolean publishAlways) {
        this.publishAlways = publishAlways;
    }
}
