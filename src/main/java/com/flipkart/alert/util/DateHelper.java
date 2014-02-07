package com.flipkart.alert.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 26/11/12
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateHelper {

    /**
     * Parse date based on date format yyyy/MM/dd-HH:mm:ss
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr) throws ParseException {
        return parseDate(dateStr, "yyyy/MM/dd-HH:mm:ss");
    }

    /**
     * Parse the date in string format based on date format and return a Date object
     * @param dateStr Example 2012-12-15:10:10:10
     * @param dateFormat Example yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr, String dateFormat) throws ParseException {
        DateFormat format = new SimpleDateFormat(dateFormat);
        return format.parse(dateStr);
    }

    /**
     * Format Date bases on given format
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static String format(Date date, String format) throws ParseException {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Return formated Date required by graphite. HH:mm_yyyyMMdd
     * @param date
     * @return
     * @throws ParseException
     */
    public static String graphiteFormat(Date date) throws ParseException {
        return format(date, "HH:mm_yyyyMMdd");
    }

    /**
     *
     * @param intervalStr 1s/1m/1h/1d for second, minute, hour and day despectively
     * @return
     */
    public static long computeIntervalInMilliSeconds(String intervalStr) {
        if(Pattern.matches("[1-9][0-9]*[smdh]",intervalStr)) {
            long multiplier = 1;
            if(intervalStr.endsWith("s"))
                multiplier = 1000;

            else if(intervalStr.endsWith("m"))
                multiplier = 60 * 1000;

            else if(intervalStr.endsWith("h"))
                multiplier = 60 * 60 * 1000;

            else if(intervalStr.endsWith("d"))
                multiplier = 24 * 60 * 60 * 1000;

            return multiplier * Integer.parseInt(intervalStr.substring(0, intervalStr.length() - 1));
        }
        throw new RuntimeException("Cannot Convert "+intervalStr+" to Milliseconds");
    }

    public static long computeIntervalInMinutes(String intervalStr) {
        if(Pattern.matches("[1-9][0-9]*[smdh]",intervalStr)) {
            long multiplier = 1;
            if(intervalStr.endsWith("s"))
                multiplier = 1000;

            else if(intervalStr.endsWith("m"))
                multiplier = 60 * 1000;

            else if(intervalStr.endsWith("h"))
                multiplier = 60 * 60 * 1000;

            else if(intervalStr.endsWith("d"))
                multiplier = 24 * 60 * 60 * 1000;

            return (multiplier * Integer.parseInt(intervalStr.substring(0, intervalStr.length() - 1)))/60000;
        }
        throw new RuntimeException("Cannot Convert "+intervalStr+" to Milliseconds");
    }

    /**
     *
     * @param howOld  1s/1m/1h/1d for second, minute, hour and day despectively
     * @return
     */
    public static Date getPastDate(String howOld) {
        return new Date(System.currentTimeMillis() - computeIntervalInMilliSeconds(howOld));
    }

    /**
     * Return formated Date required by OpenTSDB. yyyy/MM/dd-HH:mm:ss
     * @param date
     * @return
     * @throws ParseException
     */
    public static String tsdbFormat(Date date) throws ParseException{
        return format(date, "yyyy/MM/dd-HH:mm:ss");
    }
}
