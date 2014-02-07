package com.flipkart.alert.exception;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 24/10/12
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateEntityException extends Exception{
    public DuplicateEntityException(String message) {
        super(message);
    }
}
