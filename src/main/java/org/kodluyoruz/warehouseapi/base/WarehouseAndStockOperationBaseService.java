package org.kodluyoruz.warehouseapi.base;

import java.util.Date;

public interface WarehouseAndStockOperationBaseService {

    void addThisStockToTheExistingRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId);

    boolean doesThisWarehouseHaveStockOfThisProduct(Long productId, Long warehouseId);

    void addThisStockAsNewRecord(Long productId,Long warehouseId, Long stockAmount,Date date, Long userId);

    void deleteEntryWithTheseIds(Long productId, Long warehouseId);
}
