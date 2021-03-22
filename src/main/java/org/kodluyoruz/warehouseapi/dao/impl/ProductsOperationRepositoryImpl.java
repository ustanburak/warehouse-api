package org.kodluyoruz.warehouseapi.dao.impl;

import org.kodluyoruz.warehouseapi.base.AbstractOperationBaseOperationRepository;
import org.kodluyoruz.warehouseapi.dao.ProductsOperationRepository;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ProductsOperationRepositoryImpl extends AbstractOperationBaseOperationRepository<ProductEntity>
        implements ProductsOperationRepository {

    // product' un status' u deleted olarak işaretlendiği durumda o product stokta var mı diye kontrol
    @Override
    public boolean isThereAnyProductForThisIdInStock(Long productId) {
        Long result = getSession()
                .createQuery("select count(*) from product_warehouse where product_id=:productId and stock_amount>0", Long.class)
                .setParameter("productId", productId)
                .uniqueResult();
        return result > 0;
    }

    @Override
    public Collection<Summary> getProductsByWarehouseId(Long productId) {
        return getSession()
                .createQuery("select new org.kodluyoruz.warehouseapi.model.entites.Summary(w.id as WarehouseID, w.code as WarehouseCode, w.name as WarehouseName, " +
                        "p.id as ProductID, p.code as ProductCode, p.name as ProductName, p.vatRate as VatRate, p.vatAmount as VatAmount, p.price as Price, p.vatIncludedPrice as VatIncludedPrice, p.status as ProductStatus, " +
                        "pw.stockAmount as StockAmount) " +
                        "from warehouse w inner join product_warehouse pw on w.id=pw.productWarehouseId.warehouseId " +
                        "inner join product p on p.id=pw.productWarehouseId.productId " +
                        "where p.id=:productId " +
                        "ORDER BY w.id asc ", Summary.class)
                .setParameter("productId",productId)
                .getResultList();
    }
    }