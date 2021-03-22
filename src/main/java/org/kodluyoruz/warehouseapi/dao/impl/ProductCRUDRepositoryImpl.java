package org.kodluyoruz.warehouseapi.dao.impl;

import org.kodluyoruz.warehouseapi.base.AbstractBaseWarehouseAPIRepository;
import org.kodluyoruz.warehouseapi.dao.ProductCRUDRepository;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.springframework.stereotype.Repository;
@Repository
public class ProductCRUDRepositoryImpl extends AbstractBaseWarehouseAPIRepository<ProductEntity> implements ProductCRUDRepository {
}
