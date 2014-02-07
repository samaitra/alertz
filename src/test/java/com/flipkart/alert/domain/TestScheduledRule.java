package com.flipkart.alert.domain;

import com.flipkart.alert.util.DateHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
* Created by IntelliJ IDEA.
* User: nitinka
* Date: 16/12/12
* Time: 7:31 PM
* To change this template use File | Settings | File Templates.
*/
public class TestScheduledRule {
    @BeforeMethod
    public void beforeMethod() {
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testRuleWithEndDateBeforeMetricCreationDate() throws ParseException {
        RuleSchedule schedule = new RuleSchedule();
        schedule.setEndDate(new Date(System.currentTimeMillis() - 10));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("ScheduledRule Has end date before metric creation date",
                rule.canRunNow()
                , is(false));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDateInRuleDateRanges() throws ParseException {
        long now = System.currentTimeMillis();

        RuleSchedule schedule = new RuleSchedule();
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setDates(createDateRanges(new long[][]{{now, now + 24 * 60 * 60 * 1000}}));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation date in ScheduledRule dates range",
                rule.canRunNow()
                , is(true));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDateNotInRuleDateRanges() throws ParseException {
        long now = System.currentTimeMillis();

        RuleSchedule schedule = new RuleSchedule();
        schedule.setStartDate(new Date(now - 3 * 24 * 60 * 60 * 1000));
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setDates(createDateRanges(new long[][]{{now, now + 24 * 60 * 60 * 1000}}));
        schedule.setDates(createDateRanges(new long[][]{{now - 2 * 24 * 60 * 60 * 1000, now - 24 * 60 * 60 * 1000}}));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation day is in ScheduledRule Days",
                rule.canRunNow()
                ,is(false));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDayInRuleDays() throws ParseException {
        long now = System.currentTimeMillis();
        String nowDay = new SimpleDateFormat("EEEE").format(new Date(now));

        RuleSchedule schedule = new RuleSchedule();
        schedule.setStartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setDays(nowDay);

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation day in rule day",
                rule.canRunNow()
                ,is(true));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDayNotInRuleDays() throws ParseException {
        long now = System.currentTimeMillis();
        String nowDay = new SimpleDateFormat("EEEE").format(new Date(now + 24 * 60 * 60 * 1000));

        RuleSchedule schedule = new RuleSchedule();
        schedule.setStartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setDays(nowDay);

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation day not in rule day",
                rule.canRunNow()
                ,is(false));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDateInRuleTimeRanges() throws ParseException {
        long someTime = System.currentTimeMillis();

        RuleSchedule schedule = new RuleSchedule();
        schedule.setTimes(createTimeRanges(new long[][]{{someTime - 60 * 60 * 1000, someTime + 60 * 60 * 1000}}));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation time in rule times range",
                rule.canRunNow()
                ,is(true));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDateNotInRuleTimeRanges() throws ParseException {
        long now = System.currentTimeMillis();

        RuleSchedule schedule = new RuleSchedule();
        schedule.setStartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setTimes(createTimeRanges(new long[][]{{now + 60 * 60 * 1000, now + 2 * 60 * 60 * 1000}}));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation time in ScheduledRule times range",
               rule.canRunNow()
                ,is(false));
    }

    @Test(groups = {"smoke","regression","domain","ScheduledRule"})
    public void testMetricDateWithAllValidityChecks() throws ParseException {
        long now = System.currentTimeMillis();
        String someDay = new SimpleDateFormat("EEEE").format(new Date(now));

        RuleSchedule schedule = new RuleSchedule();
        schedule.setStartDate(new Date(now - 2 * 24 * 60 * 60 * 1000));
        schedule.setEndDate(new Date(now + 2 * 24 * 60 * 60 * 1000));
        schedule.setDates(createDateRanges(new long[][]{{now - 24 * 60 * 60 * 1000, now + 24 * 60 * 60 * 1000}}));
        schedule.setDays(someDay);
        schedule.setTimes(createTimeRanges(new long[][]{{now - 60 * 60 * 1000, now + 60 * 60 * 1000}}));

        ScheduledRule rule = new ScheduledRule();
        rule.setSchedule(schedule);

        assertThat("Metric creation date in ScheduledRule dates range",
                rule.canRunNow()
                ,is(true));
    }

    private String createDateRanges(long[][] dates) throws ParseException {
        String datesStr = "";
        for(long[] dateRange : dates) {
            datesStr += DateHelper.format(new Date(dateRange[0]), "yyyy/MM/dd")+
                    "-"+
                    DateHelper.format(new Date(dateRange[1]),"yyyy/MM/dd")+",";
        }
        return datesStr.substring(0, datesStr.length()-1);
    }

    private String createTimeRanges(long[][] dates) throws ParseException {
        String datesStr = "";
        for(long[] dateRange : dates) {
            datesStr += DateHelper.format(new Date(dateRange[0]),"HH:mm:ss")+
                    "-"+
                    DateHelper.format(new Date(dateRange[1]),"HH:mm:ss")+",";
        }
        return datesStr.substring(0, datesStr.length()-1);
    }
}
