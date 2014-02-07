package com.flipkart.alert.domain;

import com.yammer.dropwizard.validation.ValidationMethod;
import com.flipkart.alert.util.ResponseBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.WebApplicationException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 29/04/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule extends BaseEntity{
    private Long ruleId;

    @NotEmpty
    private String name, team;

    @Valid
    private Set<AlertVariable> variables;

    @Valid
    @NotEmpty
    private Set<RuleCheck> checks;

    @Valid
    private Set<EndPoint> endPoints;

    @JsonIgnore
    private boolean isScheduled;

    @JsonIgnore
    public List<String> endPointTypes = Arrays.asList("HTTP", "MAIL", "NAGIOS");

    @JsonIgnore
    @ValidationMethod(message = "EndPoint type should be MAIL, HTTP, NAGIOS")
    public boolean isValidType() {
        for(EndPoint endPoint : endPoints) {
            if(!endPointTypes.contains(endPoint.getType()))
                return false;
        }
        return true;
    }


    public Long getRuleId() {
        return ruleId;
    }

    public Rule setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Rule setName(String rule) {
        this.name = rule;
        return this;
    }

    public String getTeam() {
        return team;
    }

    public Rule setTeam(String team) {
        this.team = team;
        return this;
    }

    public Set<AlertVariable> getVariables() {
        return variables;
    }

    public Rule setVariables(Set<AlertVariable> variables) {
        this.variables = variables;
        return this;
    }

    public Set<EndPoint> getEndPoints() {
        return endPoints;
    }

    public Rule setEndPoints(Set<EndPoint> endPoints) {
        this.endPoints = endPoints;
        return this;
    }

    @JsonIgnore
    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Set<RuleCheck> getChecks() {
        return checks;
    }

    public void setChecks(Set<RuleCheck> checks) {
        this.checks = checks;
    }

    public void deleteEndPoints() {
        for(EndPoint endPoint : getEndPoints())
            endPoint.delete();
    }

    public void deleteChecks() {
        for(RuleCheck check : getChecks())
            check.delete();
    }

    public void deleteVariables() {
        for(AlertVariable variable : getVariables()) {
            variable.delete();
        }
    }

    public static List getAllNames(String startsWith, Class type){
        Session session = beginTransaction();
        try {
            Criteria criteria = session.createCriteria(type).add(Restrictions.like("name", startsWith + "%"));
            criteria.setProjection(Projections.property("name"));
            return criteria.list();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }
}
