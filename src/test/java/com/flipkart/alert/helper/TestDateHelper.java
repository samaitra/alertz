package com.flipkart.alert.helper;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.util.DateHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * nitinka
 * Test the DateHelper Util
 */
public class TestDateHelper {
    private static final String DATE_FORMAT = "yyyy/MM/dd-HH:mm:ss";
    private Log log = Log.forClass(TestDateHelper.class);

    @Test(groups = {"smoke", "regreesion", "helper"})
    public void testParseDate() throws ParseException {
        String inputDateStr = "2012/12/15-10:10:10";
        Date observedDate = DateHelper.parseDate(inputDateStr);
        Date expectedDate = createDateFromCalendar(2012, 12, 15, 10, 10, 10);
        assertEquals(observedDate.toString(), expectedDate.toString(),"Date");
    }

    @Test(groups = {"regression", "helper", "negative"},
            expectedExceptions = {java.text.ParseException.class}, expectedExceptionsMessageRegExp = "Unparseable date: \"2012-12-15-10:10:10\"")
    public void testParseWrongFormatDate() throws ParseException {
        String inputDateStr = "2012-12-15-10:10:10";
        DateHelper.parseDate(inputDateStr);
    }

    @Test(groups = {"smoke","regression","helper"})
    public void testDateFormat() throws ParseException {
        Date date = createDateFromCalendar(2012, 12, 15, 10, 10, 10);
        String observedFormattedDate = DateHelper.format(date, "yyyy:MM:dd");
        assertEquals(observedFormattedDate, "2012:12:15","Formatted Date");
    }

    @Test(groups = {"regression","helper", "negative"})
    public void testDateFormatToEmpty() throws ParseException {
        Date date = createDateFromCalendar(2012, 12, 15, 10, 10, 10);
        String observedFormattedDate = DateHelper.format(date, "");
        assertEquals(observedFormattedDate, "","Formatted Date");
    }

    @Test(groups = {"smoke", "regression", "helper"})
    public void testGraphiteFormattedDate() throws ParseException {
        Date date = createDateFromCalendar(2012, 12, 15, 10, 10, 10);
        String observedFormattedDate = DateHelper.graphiteFormat(date);
        assertEquals(observedFormattedDate, "10:10_20121215","Graphite Formatted Date");
    }

    @Test(groups = {"regression", "helper", "negative"},expectedExceptions = {NullPointerException.class})
    public void testNullGraphiteFormattedDate() throws ParseException {
        String observerFormattedDate = DateHelper.graphiteFormat(null);
    }

    @DataProvider(name = "computeInterval")
    public Object[][] computeIntervalTestData() {
        return new Object[][] {
                {"10s",10*1000l},
                {"10m",10*60*1000l},
                {"10h",10*60*60*1000l},
                {"10d",10*24*60*60*1000l}
        };
    }

    @Test(groups = {"smoke", "regression", "helper"},dataProvider = "computeInterval")
    public void testComputIntervalInMilliSecondsWithSeconds(String interval, long expectedIntervalInMS) throws ParseException {
        long observedIntervalInMS = DateHelper.computeIntervalInMilliSeconds(interval);
        assertEquals(observedIntervalInMS, expectedIntervalInMS,"Value in millisecond");
    }

    @Test(groups = {"regression", "helper", "negative"},expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Cannot Convert 10a to Milliseconds")
    public void testComputIntervalInMilliSecondsNonSupportedSuffix() throws ParseException {
        long observedIntervalInMS = DateHelper.computeIntervalInMilliSeconds("10a");
    }

    private Date createDateFromCalendar(int year, int month, int date, int hour, int min, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, hour, min, second);
        return calendar.getTime();
    }

}
