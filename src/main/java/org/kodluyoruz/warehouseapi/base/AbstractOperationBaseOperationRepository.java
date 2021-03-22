package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.entites.BaseEntity;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional(readOnly = true)
public abstract class AbstractOperationBaseOperationRepository<T extends BaseEntity> extends AbstractBaseEntityManager<T>
        implements WarehouseAndProductOperationBaseRepository<T>, WarehouseAndStockOperationBaseRepository {

    // create işleminde kullanılır. veritabanında girilen koda sahip olan bir kayıt var mı diye kontrol eder
    @Override
    @Transactional
    public boolean hasExistSameCode(String code) {
        Long result = getSession()
                .createQuery("select count(*) from " + entity.getName() + " where code=:givenCode", Long.class)
                .setParameter("givenCode", code)
                .uniqueResult();
        return result > 0;
    }

    // update işleminde kullanılır. veritabanında girilen koda ve id' ye sahip olan bir kayıt var mı diye kontrol eder
    // update sırasında ürünün kodu aynı kalacak şekilde bir update yapıldığı senaryosu için kontrol
    @Override
    @Transactional
    public boolean hasExistSameCodeAndId(Long id, String code) {
        Long result = getSession()
                .createQuery("select count(*) from " + entity.getName() + " where code=:givenCode and id<>:givenId", Long.class)
                .setParameter("givenCode", code)
                .setParameter("givenId", id)
                .uniqueResult();
        return result > 0;
    }

    // genericteki entity ile ilgili veritabanında herhangi bir kayıt var mı kontrol eder
    @Override
    @Transactional
    public boolean isThereAnyOfThis() {
        Long result = getSession()
                .createQuery("select count(*) from " + entity.getName(), Long.class)
                .uniqueResult();
        return result > 0;
    }

    // verilen entity id var mı ve varsa status' u active mi diye kontrol eder
    @Override
    public boolean isThereAnyActiveEntryAtThisId(Long id) {
        Long result = getSession()
                .createQuery("select count(*) from " + entity.getName() + " where id=:entityId and status=:status", Long.class)
                .setParameter("entityId", id)
                .setParameter("status", StatusEnum.ACTIVE)
                .uniqueResult();
        return result > 0;
    }

    // bahsi geçen product id stoğun ekleneceği warehouse' da var ise üzerine ekler
    @Override
    @Transactional
    public void addThisStockToTheExistingRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {
        getSession()
                .createQuery("update product_warehouse set stock_amount=:stockAmount+stock_amount, transaction_date=:date, created_by=:createdBy " +
                        "where warehouse_id=:warehouseId and product_id=:productId")
                .setParameter("stockAmount", stockAmount)
                .setParameter("date", date)
                .setParameter("createdBy", userId)
                .setParameter("warehouseId", warehouseId)
                .setParameter("productId", productId)
                .executeUpdate();
    }

    //  product_Warehouse da bu iki id' ye sahip olan bir kayıt var mı
    @Override
    public boolean doesThisWarehouseHaveStockOfThisProduct(Long productId, Long warehouseId) {
        Long result = getSession()
                .createQuery("select count(*) from product_warehouse where product_id=:productId and warehouse_id=:warehouseId", Long.class)
                .setParameter("productId", productId)
                .setParameter("warehouseId", warehouseId)
                .uniqueResult();
        return result > 0;
    }

    // yeni bir kayıt olarak ProductWarehouse tablosune veri ekleme
    @Override
    @Transactional
    public void addThisStockAsNewRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {

        getSession().createNativeQuery("insert into product_warehouse values (?,?,?,?,?)")
                .setParameter(1, productId)
                .setParameter(2, warehouseId)
                .setParameter(3, date)
                .setParameter(4, userId)
                .setParameter(5, stockAmount)
                .executeUpdate();
    }

    // bu iki id' ye sahip olan kayıtları product_Warehouse' dan sil
    @Override
    @Transactional
    public void deleteEntryWithTheseIds(Long productId, Long warehouseId) {
        getSession()
                .createQuery("delete product_warehouse where product_id=:productId and warehouse_id=:warehouseId")
                .setParameter("productId", productId)
                .setParameter("warehouseId", warehouseId)
                .executeUpdate();
    }
}