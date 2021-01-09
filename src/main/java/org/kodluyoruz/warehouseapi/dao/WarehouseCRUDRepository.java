package org.kodluyoruz.warehouseapi.dao;

import org.kodluyoruz.warehouseapi.model.entites.WarehouseEntity;

import java.util.Collection;

public interface WarehouseCRUDRepository {

    Collection<WarehouseEntity> list();

    WarehouseEntity create(WarehouseEntity warehouseEntity);

    WarehouseEntity update(WarehouseEntity warehouseEntity);

    void delete(Long id);


}
