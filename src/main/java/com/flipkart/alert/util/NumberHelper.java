package com.flipkart.alert.util;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 14/1/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberHelper {
    public static boolean isNumber(String numStr) {
        try{
            int num = Integer.parseInt(numStr);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
