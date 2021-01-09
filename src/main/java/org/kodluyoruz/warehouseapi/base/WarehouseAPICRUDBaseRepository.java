package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;

import java.util.Collection;

public interface WarehouseAPICRUDBaseRepository<T extends BaseEntity, ID extends Long> {

    Collection<T> list();

    T create(T entity);

    T update(T entity);

    void delete(ID id);

}
