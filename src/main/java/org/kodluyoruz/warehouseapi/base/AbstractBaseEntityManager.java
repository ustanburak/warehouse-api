package org.kodluyoruz.warehouseapi.base;

import org.hibernate.Session;
import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;

public class AbstractBaseEntityManager<T extends BaseEntity> {
    @PersistenceContext
    private EntityManager entityManager;
    protected Class<T> entity;

    @SuppressWarnings("unchecked")
    public AbstractBaseEntityManager() {
        this.entity = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
