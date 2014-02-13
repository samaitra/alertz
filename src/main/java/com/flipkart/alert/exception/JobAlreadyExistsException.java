package com.flipkart.alert.exception;

import org.quartz.JobKey;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 22/11/12
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class JobAlreadyExistsException extends Exception{
    public JobAlreadyExistsException(JobKey jobKey) {
        super(jobKey.getName()+" already exists");
    }
}
