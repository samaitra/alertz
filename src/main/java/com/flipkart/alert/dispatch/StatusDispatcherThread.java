package com.flipkart.alert.dispatch;

import com.yammer.dropwizard.logging.Log;
import com.flipkart.alert.domain.Alert;
import com.flipkart.alert.domain.DispatcherContainer;
import com.flipkart.alert.domain.Rule;

/**
 * Created by IntelliJ IDEA.
 * User: deepthi
 * Date: 19/04/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusDispatcherThread implements Runnable{

    private static Log log = Log.forClass(StatusDispatcherThread.class);

    @Override
    public void run() {
            try {
                DispatcherContainer container = StatusDispatchQueue.remove();
                StatusDispatcher dispatcher = container.getDispatcher();
                Object dispatcherObject = container.getDispatcherObject();

                if(dispatcherObject instanceof Alert)
                    dispatcher.dispatch((Alert) dispatcherObject, container.getConfigurableParameters());
                else if(container.isPublishAlways())
                    container.getDispatcher().dispatch((Rule) dispatcherObject, container.getConfigurableParameters());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

}
