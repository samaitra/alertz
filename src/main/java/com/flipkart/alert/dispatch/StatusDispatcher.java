package com.flipkart.alert.dispatch;

import com.flipkart.alert.domain.Alert;
import com.flipkart.alert.domain.Rule;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 17/04/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public interface StatusDispatcher {

    abstract public void dispatch(Alert alert, Map<String, Object>configParameters);

    abstract public void dispatch(Rule rule, Map<String, Object> configParameters);

}
