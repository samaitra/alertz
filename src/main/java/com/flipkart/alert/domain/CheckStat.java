package com.flipkart.alert.domain;

import com.flipkart.alert.exception.DuplicateEntityException;
import com.flipkart.alert.util.ResponseBuilder;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.ws.rs.WebApplicationException;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 04/07/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
public class CheckStat extends BaseEntity{

    @JsonIgnore
    private long checkId;

    @JsonIgnore
    private RuleStat ruleStat;

    private boolean breached;

    private String description;

    private String expression;


    public CheckStat create() {
        CheckStat existingStat = getById(CheckStat.class, this.checkId);
        if(existingStat != null) {
           super.update();
        } else {
            try {
                return (CheckStat) super.create();
            } catch (DuplicateEntityException e) {
                throw new WebApplicationException(ResponseBuilder.duplicate(e.getMessage()));
            }
        }
        return existingStat;
    }

    private CheckStat(CheckStatBuilder builder) {
        this.checkId = builder.checkId;
        this.ruleStat = builder.ruleStat;
        this.breached = builder.breached;
        this.description = builder.description;
        this.expression = builder.expression;
    }

    public long getCheckId() {
        return checkId;
    }

    public void setCheckId(long checkId) {
        this.checkId = checkId;
    }

    public RuleStat getRuleStat() {
        return ruleStat;
    }

    public void setRuleStat(RuleStat ruleStat) {
        this.ruleStat = ruleStat;
    }

    public boolean isBreached() {
        return breached;
    }

    public void setBreached(boolean breached) {
        this.breached = breached;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    public static class CheckStatBuilder {
        private long checkId;
        private RuleStat ruleStat;
        private boolean breached;
        private String description;
        private String expression;

        public CheckStatBuilder withCheckId(long checkId) {
            this.checkId = checkId;
            return this;
        }

        public CheckStatBuilder withRuleStat(RuleStat ruleStat) {
            this.ruleStat = ruleStat;
            return this;
        }

        public CheckStatBuilder withBreached(boolean breached) {
            this.breached = breached;
            return this;
        }

        public CheckStatBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CheckStatBuilder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public CheckStat build() {
            return new CheckStat(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CheckStat)) return false;

        CheckStat that = (CheckStat) o;
        if(this.breached != that.breached) return false;
        if(!this.description.equals(that.description)) return false;
        if(!this.expression.equals(that.expression)) return false;
        return true;
    }
}
