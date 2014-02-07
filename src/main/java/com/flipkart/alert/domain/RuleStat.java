package com.flipkart.alert.domain;

import com.flipkart.alert.exception.DuplicateEntityException;
import com.flipkart.alert.util.ResponseBuilder;
import com.flipkart.alert.util.SetHelper;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.ws.rs.WebApplicationException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 04/07/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
public class RuleStat extends BaseEntity{

    @JsonIgnore
    private long ruleId;

    private Date lastCheckTime = new Date();

    private boolean breached;

    private Set<CheckStat> checkStats;

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public boolean isBreached() {
        return breached;
    }

    public void setBreached(boolean breached) {
        this.breached = breached;
    }

    public Set<CheckStat> getCheckStats() {
        return checkStats;
    }

    public void setCheckStats(Set<CheckStat> checkStats) {
        this.checkStats = checkStats;
    }

    public RuleStat create(){
        RuleStat existingStat = getById(RuleStat.class, this.ruleId);
        if(existingStat != null) {
           super.update();
        } else {
            try {
                return (RuleStat) super.create();
            } catch (DuplicateEntityException e) {
                throw new WebApplicationException(ResponseBuilder.duplicate(e.getMessage()));
            }
        }
        return existingStat;
    }

    public static List<RuleStat> getAllStats() {
        return getAll(RuleStat.class);
    }

    public static List<RuleStat> getStatsForTeam(String teamName, Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<RuleStat> stats = new ArrayList<RuleStat>();
        List rules = getByColumn(clazz, "team", teamName);
        for (Object rule : rules) {
            RuleStat ruleStat = getById(RuleStat.class, (Serializable) rule.getClass().getMethod("getRuleId").invoke(rule));
            if(ruleStat != null) {
                stats.add(ruleStat);
            }
        }
        return stats;
    }

    private RuleStat(RuleStatBuilder builder) {
        this.ruleId = builder.ruleId;
        this.lastCheckTime = (builder.lastCheckTime == null) ? new Date() : builder.lastCheckTime;
        this.breached = builder.breached;
        this.checkStats = builder.checkStats;
    }

    public static class RuleStatBuilder {

        private long ruleId;
        private Date lastCheckTime;
        private boolean breached;
        private Set<CheckStat> checkStats;

        public RuleStatBuilder withRuleId(long ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        public RuleStatBuilder withLastCheckTime(Date lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
            return this;
        }

        public RuleStatBuilder withBreached(boolean breached) {
            this.breached = breached;
            return this;
        }

        public RuleStatBuilder withCheckStats(Set<CheckStat> stats) {
            this.checkStats = stats;
            return this;
        }

        public RuleStat build() {
            return new RuleStat(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleStat that = (RuleStat) o;

        if (breached != that.breached) return false;
        if (ruleId != that.ruleId) return false;
        if (!SetHelper.setMatches(this.checkStats, that.checkStats)) return false;

        return true;
    }

}