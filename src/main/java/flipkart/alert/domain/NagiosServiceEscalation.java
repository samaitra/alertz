package flipkart.alert.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: arjunkumar
 * Date: 9/5/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class NagiosServiceEscalation {
    private String contact_groups;
    private int last_notification;
    private int first_notification;
    private int notification_interval;
    private String service_description;

    public String getContact_groups() {
        return contact_groups;
    }

    public void setContact_groups(String contact_groups) {
        this.contact_groups = contact_groups;
    }

    public int getLast_notification() {
        return last_notification;
    }

    public void setLast_notification(int last_notification) {
        this.last_notification = last_notification;
    }

    public int getFirst_notification() {
        return first_notification;
    }

    public void setFirst_notification(int first_notification) {
        this.first_notification = first_notification;
    }

    public int getNotification_interval() {
        return notification_interval;
    }

    public void setNotification_interval(int notification_interval) {
        this.notification_interval = notification_interval;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    @Override
    public String toString() {
        return "NagiosServiceEscalation{" +
                "contact_groups='" + contact_groups + '\'' +
                ", last_notification=" + last_notification +
                ", first_notification=" + first_notification +
                ", notification_interval=" + notification_interval +
                ", service_description=" + service_description +
                '}';
    }
}
