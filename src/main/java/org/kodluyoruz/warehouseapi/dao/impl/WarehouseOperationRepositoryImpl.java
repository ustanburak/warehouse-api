package org.kodluyoruz.warehouseapi.dao.impl;

import org.kodluyoruz.warehouseapi.base.AbstractOperationBaseOperationRepository;
import org.kodluyoruz.warehouseapi.dao.WarehouseOperationRepository;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseSummary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional(readOnly = true)
public class WarehouseOperationRepositoryImpl extends AbstractOperationBaseOperationRepository<WarehouseEntity>
        implements WarehouseOperationRepository {

    // warehouse' un status' u deleted olarak işaretlendiği durumda o warehouse' a ilişkin stokta ürün var mı var mı diye kontrol
    @Override
    public boolean isThereAnyProductForThisWarehouse(Long warehouseId) {
        Long result = getSession()
                .createQuery("select count(*) from product_warehouse where warehouse_id=:warehouseId and stock_amount>0", Long.class)
                .setParameter("warehouseId", warehouseId)
                .uniqueResult();
        return result > 0;
    }

    // o warehouse id' ye ait stok bilgilerini getirir
    @Override
    public Collection<ProductWarehouseEntity> getStocksFromThisWarehouse(Long warehouseId) {
        return getSession()
                .createQuery("from " + ProductWarehouseEntity.class.getName() + "  where warehouse_id=:warehouseId", ProductWarehouseEntity.class)
                .setParameter("warehouseId", warehouseId)
                .getResultList();
    }

    @Override
    public Collection<Summary> getProductsByWarehouseId(Long warehouseId) {
        return getSession()
                .createQuery("select new org.kodluyoruz.warehouseapi.model.entites.Summary(w.id as WarehouseID, w.code as WarehouseCode, w.name as WarehouseName, " +
                        "p.id as ProductID, p.code as ProductCode, p.name as ProductName, p.vatRate as VatRate, p.vatAmount as VatAmount, p.price as Price, p.vatIncludedPrice as VatIncludedPrice, p.status as ProductStatus, " +
                        "pw.stockAmount as StockAmount) " +
                        "from warehouse w " +
                        "inner join product_warehouse pw on w.id=pw.productWarehouseId.warehouseId " +
                        "inner join product p on p.id=pw.productWarehouseId.productId " +
                        "where w.id=:warehouseId " +
                        "ORDER BY w.id asc ", Summary.class)
                .setParameter("warehouseId",warehouseId)
                .getResultList();
    }

    @Override
    public WarehouseSummary getSummaryOfThisWarehouse(Long warehouseId) {
        return getSession()
                .createQuery("select count(p.id) as NumberOfProductTypes, sum(pw.stockAmount) as TotalStockQuantityOfAllProducts " +
                        "FROM PRODUCT_WAREHOUSE pw " +
                        "inner join product p on p.id = pw.productWarehouseId.productId " +
                        "inner join warehouse w on w.id = pw.productWarehouseId.warehouseId " +
                        "where w.id=:warehouseId ", WarehouseSummary.class)
                .setParameter("warehouseId",warehouseId)
                .getSingleResult();
    }

    // yeni bir kayıt olarak ProductWarehouse tablosune veri ekleme
    /*@Override
    @Transactional
    public void addThisStockAsNewRecord(ProductWarehouseEntity productWarehouseEntity) {
        getSession().persist(productWarehouseEntity);
    }*/
}