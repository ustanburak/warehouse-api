package org.kodluyoruz.warehouseapi.service;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.base.WarehouseAndProductOperationBaseService;
import org.kodluyoruz.warehouseapi.base.WarehouseAndStockOperationBaseService;
import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseSummary;

import java.util.Collection;

public interface WarehouseOperationService extends WarehouseAndProductOperationBaseService<BaseIDDTO>, WarehouseAndStockOperationBaseService {

    boolean isThereAnyProductForThisWarehouse(Long warehouseId);

    WarehouseAPIResponseHolder<?> transferAllProducts(Long fromWarehouseId, Long toWarehouseId);

    Collection<ProductWarehouseEntity> getStocksFromThisWarehouse(Long warehouseId);

    WarehouseAPIResponseHolder<Collection<Summary>> getProductsByWarehouseId(Long warehouseId);

    WarehouseAPIResponseHolder<WarehouseSummary> getSummaryOfThisWarehouse(Long warehouseId);

}