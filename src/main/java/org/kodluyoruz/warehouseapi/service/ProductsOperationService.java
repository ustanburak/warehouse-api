package org.kodluyoruz.warehouseapi.service;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.base.WarehouseAndProductOperationBaseService;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.model.entites.Summary;

import java.util.Collection;

public interface ProductsOperationService extends WarehouseAndProductOperationBaseService<ProductDTO> {
    boolean isThereAnyProductForThisIdInStock(Long id);

    WarehouseAPIResponseHolder<Collection<Summary>> getProductsAndWarehousesByProductId(Long productId);
}
