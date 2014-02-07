package com.flipkart.alert.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 21/10/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */

@Setter @Getter
public class DataArchivalConfiguration {
    private boolean enabled;
    private ArrayList<String> hosts;
}
