package org.kodluyoruz.warehouseapi.dao.impl;

import org.kodluyoruz.warehouseapi.base.AbstractWarehouseAPIRepository;
import org.kodluyoruz.warehouseapi.dao.WarehouseCRUDRepository;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class WarehouseCRUDRepositoryImpl extends
        AbstractWarehouseAPIRepository<WarehouseEntity, Long> implements WarehouseCRUDRepository {

}
