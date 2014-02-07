package com.flipkart.alert.health;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 26/10/12
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */
import com.yammer.metrics.core.HealthCheck;
import com.flipkart.alert.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class DBHealthCheck extends HealthCheck {

    public DBHealthCheck(String name) {
        super(name);
    }

    @Override
    protected Result check() throws Exception {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            if(session.isConnected())
                return Result.healthy("Could Connect to Database");
            else
                return Result.healthy("Couldn't Connect to Database");
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return Result.unhealthy(e);
        }
        finally {
            if(session != null)
                session.close();
        }
    }
}