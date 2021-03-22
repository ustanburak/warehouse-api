package org.kodluyoruz.warehouseapi.dao;

import org.kodluyoruz.warehouseapi.base.WarehouseAndProductOperationBaseRepository;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;

import java.util.Collection;

public interface ProductsOperationRepository extends WarehouseAndProductOperationBaseRepository<ProductEntity> {
    boolean isThereAnyProductForThisIdInStock(Long id);

    Collection<Summary> getProductsByWarehouseId(Long productId);
}