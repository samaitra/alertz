package com.flipkart.alert.domain;

import com.yammer.dropwizard.validation.ValidationMethod;
import com.flipkart.alert.util.DateHelper;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Time;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class RuleSchedule extends BaseEntity{
    private Long ruleId;
    private Date startDate = new Date(), endDate;
    private String dates;
    private String days;
    private String times;
    @NotEmpty
    private String interval;

    @JsonIgnore
    private ScheduledRule rule;

    private static final List<String> VALID_DAYS = Arrays.asList(new String[]{"WEEKDAY",
            "WEEKEND",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY",
            "SUNDAY"});

    private static final String FORMAT_DATE = "\\d\\d\\d\\d/\\d\\d/\\d\\d-\\d\\d\\d\\d/\\d\\d/\\d\\d";
    private static final String FORMAT_TIME = "\\d\\d:\\d\\d:\\d\\d-\\d\\d:\\d\\d:\\d\\d";

    public ScheduledRule getRule() {
        return rule;
    }

    public RuleSchedule setRule(ScheduledRule rule) {
        this.rule = rule;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public RuleSchedule setStartDate(Date startDate){
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public RuleSchedule setEndDate(Date endDate){
        this.endDate = endDate;
        return this;
    }

    public String getDays() {
        return days;
    }

    public RuleSchedule setDays(String days) {
        if(days != null)
            this.days = days.replace("WEEKDAY","MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY").
                    replace("WEEKEND", "SATURDAY,SUNDAY").
                    toUpperCase();
        return this;
    }

    public String getDates() {
        return dates;
    }

    public RuleSchedule setDates(String dates) {
        this.dates = dates;
        return this;
    }

    public String getTimes() {
        return times;
    }

    public RuleSchedule setTimes(String times) {
        this.times = times;
        return this;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @JsonIgnore
    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    @JsonIgnore
    @ValidationMethod(message = "days should be proper WEEKEND, WEEKDAY")
    public boolean isValidDays(){
        if(days != null) {
            String[] dayTokens = days.split(",");
            for(String day : dayTokens) {
                if(!VALID_DAYS.contains(day))
                    return false;
            }
        }
        return true;
    }

    @JsonIgnore
    @ValidationMethod(message = "endDate should be less than startDate")
    public boolean isValidEndDate(){
        if(endDate != null && startDate != null) {
            return endDate.getTime() > startDate.getTime();
        }
        return true;
    }

    @JsonIgnore
    @ValidationMethod(message = "times should have 00:00:00-00:00:00 format separated with , and startTime should be less then endTime")
    public boolean isValidTimes(){
        if(times != null) {
            String[] timeTokens = times.split(",");
            for(String time : timeTokens) {
                if(Pattern.matches(FORMAT_TIME, time)) {
                    Time startTime = Time.valueOf(time.split("-")[0]);
                    Time endTime = Time.valueOf(time.split("-")[1]);
                    if(startTime.after(endTime))
                        return false;
                }
                else
                    return false;
            }
        }
        return true;
    }

    @JsonIgnore
    @ValidationMethod(message = "dates should have 2012/11/10-2012/11/12 format separated with , and startDate should be less then endDate and dates should be in between Valdity startDate and EndDate")
    public boolean isValidDates() throws ParseException {
        if(dates != null) {
            String[] dateTokens = dates.split(",");
            for(String date : dateTokens) {
                if(Pattern.matches(FORMAT_DATE, date)) {
                    Date rangeStartDate = DateHelper.parseDate(date.split("-")[0], "yyyy/MM/dd");
                    Date rangeEndDate = DateHelper.parseDate(date.split("-")[1], "yyyy/MM/dd");
                    if(rangeStartDate.after(rangeEndDate))
                        return false;

                    if(rangeStartDate.before(startDate) || rangeStartDate.after(endDate))
                        return false;

                    if(rangeEndDate.before(startDate) || rangeEndDate.after(endDate))
                        return false;

                }
                else
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleSchedule)) return false;

        RuleSchedule that = (RuleSchedule) o;

        if (dates != null ? !dates.equals(that.dates) : that.dates != null) return false;
        if (days != null ? !days.equals(that.days) : that.days != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (interval != null ? !interval.equals(that.interval) : that.interval != null) return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
//        if (ruleId != null ? !ruleId.equals(that.ruleId) : that.ruleId != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (times != null ? !times.equals(that.times) : that.times != null) return false;

        return true;
    }

}
