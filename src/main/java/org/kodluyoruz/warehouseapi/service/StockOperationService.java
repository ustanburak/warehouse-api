package org.kodluyoruz.warehouseapi.service;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.base.WarehouseAndStockOperationBaseService;
import org.kodluyoruz.warehouseapi.model.dto.ProductWarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.Summary;

import java.util.Collection;

public interface StockOperationService extends WarehouseAndStockOperationBaseService {
    WarehouseAPIResponseHolder<?> transferThisProduct(Long productId, Long fromWarehouseId, Long toWarehouseId);

    WarehouseAPIResponseHolder<?> updateProductStockQuantity(ProductWarehouseDTO productWarehouseDTO);

    Long getStockOfGivenProductFromThisWarehouse(Long productId, Long warehouseId);

    WarehouseAPIResponseHolder<Collection<Summary>> getSummaries();

    WarehouseAPIResponseHolder<?> addNewStock(ProductWarehouseDTO productWarehouseDTO);
}