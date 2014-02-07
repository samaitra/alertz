package flipkart.alert.domain;

import flipkart.alert.exception.DuplicateEntityException;
import flipkart.alert.util.ResponseBuilder;
import flipkart.alert.util.SetHelper;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.Session;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.WebApplicationException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ScheduledRule extends Rule{

    private RuleSchedule schedule;

    private String alertQueue;

    @Valid
    @NotEmpty
    private Set<DataSeries> dataSerieses;

    public RuleSchedule getSchedule() {
        return schedule;
    }

    public ScheduledRule setSchedule(RuleSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public ScheduledRule create(){
        if(this.schedule != null)
            this.schedule.setRule(this);
        try {
            return (ScheduledRule) super.create();
        } catch (DuplicateEntityException e) {
            throw new WebApplicationException(ResponseBuilder.duplicate(e.getMessage()));
        }
    }

    public String getAlertQueue() {
        return alertQueue == null ? getName()+"-"+getTeam() : alertQueue;
    }

    public void setAlertQueue(String alertQueue) {
        this.alertQueue = alertQueue;
    }

    public Set<DataSeries> getDataSerieses() {
        return dataSerieses;
    }

    public ScheduledRule setDataSerieses(Set<DataSeries> dataSerieses) {
        this.dataSerieses = dataSerieses;
        return this;
    }

    public void update(ScheduledRule withRule) {
        Session session = beginTransaction();
        this.deleteSchedule();
        this.deleteChecks();
        this.deleteDataSerieses();
        this.deleteVariables();
        this.deleteEndPoints();
        withRule.setRuleId(getRuleId());
        withRule.schedule.setRule(withRule);
        withRule.update();
        commitTransaction(session);
    }

    private void deleteDataSerieses() {
        for(DataSeries series : getDataSerieses())
            series.delete();
    }


    private void deleteSchedule() {
        this.getSchedule().delete();
    }

    public static List<String> getTeams() {
        Session session = beginTransaction();
        List<String> teams = session.createSQLQuery("select distinct(TEAM) from RULES where IS_SCHEDULED=1").list();
        commitTransaction(session);
        return teams;
    }

    @JsonIgnore
    public boolean canRunNow() throws ParseException {
        Date now = new Date();

        //rule is expired or not
        if(schedule.getEndDate() != null && schedule.getEndDate().getTime() < now.getTime())
            return false;

        // If current date is in any dates range
        if(schedule.getDates() != null) {
            boolean isMetricDateInRange = false;
            String dates = schedule.getDates();
            for(String dateRange : dates.split(",")) {
                Date rangeStartDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateRange.split("-")[0]);
                Date rangeEndDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateRange.split("-")[1]);

                if(now.after(rangeStartDate) && now.before(rangeEndDate)) {
                    isMetricDateInRange = true;
                    break;
                }
            }
            if(!isMetricDateInRange)
                return false;
        }

        // If its Right Day
        //String currentDay = currentDate.
        if(schedule.getDays() != null ){
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
            if(!schedule.getDays().contains(simpleDateformat.format(now).toUpperCase()))
                return false;
        }


        // If current Time in Range
        if(schedule.getTimes() != null) {
            boolean isMetricTimeInRange = false;
            Time metricTime = Time.valueOf(new SimpleDateFormat("HH:mm:ss").format(now));
            String times = schedule.getTimes();
            for(String timeRange : times.split(",")) {
                Time rangeStartTime = Time.valueOf(timeRange.split("-")[0]);
                Time rangeEndTime = Time.valueOf(timeRange.split("-")[1]);
                if(metricTime.after(rangeStartTime) && metricTime.before(rangeEndTime)) {
                    isMetricTimeInRange = true;
                    break;
                }
            }
            if(!isMetricTimeInRange)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduledRule)) return false;

        ScheduledRule that = (ScheduledRule) o;

        if (this.getAlertQueue() != null ? !this.getAlertQueue().equals(that.getAlertQueue()) : that.getAlertQueue() != null) return false;

        if(!SetHelper.setMatches(getChecks(), that.getChecks()))
            return false;

        if(!SetHelper.setMatches(getDataSerieses(), that.getDataSerieses()))
            return false;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
//        if (ruleId != null ? !ruleId.equals(that.ruleId) : that.ruleId != null) return false;
        if (schedule != null ? !schedule.equals(that.schedule) : that.schedule != null) return false;
        if (getTeam() != null ? !getTeam().equals(that.getTeam()) : that.getTeam() != null) return false;

        return true;
    }

    public static List<ScheduledRule> getRules(String teamName, String metricName) {
        List<ScheduledRule> allRules = getAll(ScheduledRule.class);

        // Just a Quick Solution
        List<ScheduledRule> matchingRules = new ArrayList<ScheduledRule>();
        for(ScheduledRule rule : allRules) {
            if(rule.getTeam().contains(teamName)) {
                for(RuleCheck check : rule.getChecks()) {
                    if(check.getBooleanExpression().contains(metricName)) {
                        matchingRules.add(rule);
                        break;
                    }
                }
            }
        }

        return matchingRules;

    }
}