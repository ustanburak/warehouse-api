package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

@Transactional(readOnly = true)
public abstract class AbstractBaseWarehouseAPIRepository<T extends BaseEntity> extends AbstractBaseEntityManager<T>
        implements WarehouseAPICRUDBaseRepository<T> {

    @Override
    public Collection<T> list() {
        return getSession()
                .createQuery("from " + entity.getName(), entity)
                //.createQuery("from " + entity.getName() + " where status=:status", entity)
                //.setParameter("status", StatusEnum.ACTIVE)
                .getResultList();
    }

    @Override
    public T getById(Long id) {
        return getSession()
                .createQuery("from " + entity.getName() + " where id=:id", entity)
                .setParameter("id", id)
                .getSingleResult();
    }

    /**
     * persist metodu ilgili entity' yi database' e kayıt etmemize yarıyor. herhangi bir obje alır. bu bir entity ise
     * ilgili tabloyu bulur ve o nesneyi kayıt eder. database' de create, update ve delete metodları için transactional açılması
     * zorunludur.
     */
    @Override
    @Transactional
    public T create(T entity) {
        getSession().persist(entity);
        return entity;
    }

    // update metodu da aynı şekilde
    @Override
    @Transactional
    public T update(T entity) {
        getSession().update(entity);
        return entity;
    }

    // delete işleminde datalar fiziksel olarak silinmez. status bilgisini deleted' a çekiyoruz. o id' ye sahip olan nesne için
    @Override
    @Transactional
    public void delete(Long id) {
        getSession()
                .createQuery("update " + entity.getName() + " set status =:newStatus, updated_date=:date where id=:entityId")
                .setParameter("entityId", id)
                .setParameter("newStatus", StatusEnum.DELETED)
                .setParameter("date", new Date())
                .executeUpdate();
    }
}