package com.flipkart.alert.domain;

import com.flipkart.alert.exception.DuplicateEntityException;
import com.flipkart.alert.util.HibernateUtil;
import com.flipkart.alert.util.ResponseBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.WebApplicationException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 23/10/12
 * Time: 6:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseEntity {
    public BaseEntity create() throws DuplicateEntityException {
        Session session = HibernateUtil.getSession();
        Transaction txn = null;

        try {
            txn = session.beginTransaction();
            session.save(this);
            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            if(txn!=null) txn.rollback();
            if(e.getMessage().contains("Duplicate entry")) {
                throw new DuplicateEntityException(e.getLocalizedMessage());
            }
            throw new WebApplicationException(ResponseBuilder.badRequest(e.getMessage()));
        }finally {
            session.close();
        }
        return this;
    }

    /**
     * Search instance by id for a particular class representing the DB Entity
     * @param type
     * @param id
     * @param <T>
     * @return
     */
    public static <T> T getById(Class<T> type, Serializable id) {
        Session session = beginTransaction();
        try {
            return (T) session.byId(type).load(id);
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public static <T> List<T> getByColumn(Class<T> type, String columnName, Object columnValue) {
        Session session = beginTransaction();
        try {
            Criteria criteria = session.createCriteria(type).add(Restrictions.eq(columnName, columnValue));
            return criteria.list();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public static <T> List<T> getByColumnMatcher(Class<T> type, String columnName, Object columnValue) {
        Session session = beginTransaction();
        try {
            Criteria criteria = session.createCriteria(type).add(Restrictions.like(columnName, columnValue));
            return criteria.list();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }

    }

    /**
     * All Instances of Entities represented by class type
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> getAll(Class<T> type) {
        Session session = beginTransaction();
        try {
            return (List<T>)session.createQuery("from "+type.getSimpleName()).list();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    /**
     * Fill current Entity with new one. Used in updating existing entities
     * @param typeObject
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    protected <T>T fill(T typeObject) {
        Field[] fields = typeObject.getClass().getDeclaredFields();
        for(Field field : fields){
            try {
                field.setAccessible(true);
                System.out.println("Old Value '"+field.get(this)+"'");
                if(field.get(typeObject) != null)
                    field.set(this, field.get(typeObject));
                System.out.println("New Value '"+field.get(this)+"'");
            }
            catch(IllegalAccessException n) {
            }
        }
        return (T)this;
    }

    synchronized public static <T>  T update(Serializable id, T newEntity) {
        Session session = beginTransaction();

        try {
            T oldEntity = (T) getById(newEntity.getClass(), id);
            if(oldEntity == null)
                throw new WebApplicationException(ResponseBuilder.notFound("Entity With id "+id+ "not found"));
            session.update(((BaseEntity)oldEntity).fill(newEntity));
            return oldEntity;
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public void delete() {
        Session session = beginTransaction();
        try {
            session.delete(this);
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public void update() {
        Session session = HibernateUtil.getSession();
        Transaction txn = null;

        try {
            txn = session.beginTransaction();
            session.update(this);
            txn.commit();
        }
        catch (Exception e) {
            if(txn!=null) txn.rollback();
            throw new WebApplicationException(ResponseBuilder.badRequest(e.getMessage()));
        }finally {
            session.close();
        }
    }

    public static void deleteAll(String tableName) {
        Session session = beginTransaction();

        try {
            session.createSQLQuery("delete from "+tableName).executeUpdate();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public static void executeUpdate(String query) {
        Session session = beginTransaction();

        try {
            session.createSQLQuery(query).executeUpdate();
        }
        catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
        finally {
            commitTransaction(session);
        }
    }

    public static Session beginTransaction() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        return session;
    }

    public static void commitTransaction(Session session) {
        if(session != null) {
            if(session.getTransaction().isActive())
                session.getTransaction().commit();
            if(session.isOpen())
                session.close();
        }
    }
}
