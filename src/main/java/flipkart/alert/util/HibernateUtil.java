package flipkart.alert.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/10/12
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    static {
        try {
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}
