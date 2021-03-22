package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;

import java.util.Collection;

public interface WarehouseAPICRUDBaseRepository<T extends BaseEntity> {

    Collection<T> list();

    T getById(Long id);

    T create(T entity);

    T update(T entity);

    void delete(Long id);

}
