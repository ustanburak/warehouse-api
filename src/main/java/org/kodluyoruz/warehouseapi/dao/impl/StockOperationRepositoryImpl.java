package org.kodluyoruz.warehouseapi.dao.impl;


import org.kodluyoruz.warehouseapi.base.AbstractOperationBaseOperationRepository;
import org.kodluyoruz.warehouseapi.dao.StockOperationRepository;
import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

@Repository
@Transactional(readOnly = true)
public class StockOperationRepositoryImpl extends AbstractOperationBaseOperationRepository<BaseEntity> implements StockOperationRepository {

    @Override
    public Long getStockOfGivenProductFromThisWarehouse(Long productId, Long warehouseId) {
        return getSession()
                .createQuery("select stockAmount from product_warehouse where product_id=:productId and warehouse_id=:warehouseId", Long.class)
                .setParameter("productId", productId)
                .setParameter("warehouseId", warehouseId)
                .uniqueResult();
    }

    // entity göndererek stok güncelleme
    @Override
    @Transactional
    public ProductWarehouseEntity updateProductStockQuantity(ProductWarehouseEntity productWarehouseEntity) {
        getSession().update(productWarehouseEntity);
        return productWarehouseEntity;
    }

    // DOĞRUDAN DEĞERLERİ GÖNDEREREK STOCK GÜNCELLEME. ÜSTTEKİ METOTLA AYNI İŞİ YAPIYOR
    @Override
    @Transactional
    public void updateProductStockQuantity(Long productId, Long warehouseId, Long stockAmount, Date date, Long createdBy) {
                 getSession()
                .createQuery("update product_warehouse set stock_amount=:stockAmount, transaction_date=:date, created_by=:createdBy " +
                        "where warehouse_id=:warehouseId and product_id=:productId")
                .setParameter("stockAmount", stockAmount)
                .setParameter("date", date)
                .setParameter("createdBy", createdBy)
                .setParameter("warehouseId", warehouseId)
                .setParameter("productId", productId)
                .executeUpdate();
    }

    // TODO- şimdilik beirli değerler alınıyor. sonradan düzenlenebilir
    @Override
    public Collection<Summary> getSummaries() {
        return getSession()
                .createQuery("select new org.kodluyoruz.warehouseapi.model.entites.Summary(w.id as WarehouseID, w.code as WarehouseCode, w.name as WarehouseName, " +
                        "p.id as ProductID, p.code as ProductCode, p.name as ProductName, p.vatRate as VatRate, p.vatAmount as VatAmount, p.price as Price, p.vatIncludedPrice as VatIncludedPrice, p.status as ProductStatus, " +
                        "pw.stockAmount as StockAmount) " +
                        "from warehouse w inner join product_warehouse pw on w.id=pw.productWarehouseId.warehouseId " +
                        "inner join product p on p.id=pw.productWarehouseId.productId " +
                        "ORDER BY w.id asc ", Summary.class)
                .getResultList();
    }

}