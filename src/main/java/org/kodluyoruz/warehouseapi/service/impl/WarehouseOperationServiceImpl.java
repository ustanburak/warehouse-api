package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseError;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.dao.WarehouseOperationRepository;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.model.entites.WarehouseSummary;
import org.kodluyoruz.warehouseapi.service.WarehouseOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class WarehouseOperationServiceImpl implements WarehouseOperationService {

    private final WarehouseOperationRepository warehouseOperationRepository;

    @Override
    public boolean hasExistSameCode(String code) {
        return warehouseOperationRepository.hasExistSameCode(code);
    }

    @Override
    public boolean hasExistSameCodeAndId(Long id, String code) {
        return warehouseOperationRepository.hasExistSameCodeAndId(id, code);
    }

    @Override
    public boolean isThereAnyOfThis() {
        return warehouseOperationRepository.isThereAnyOfThis();
    }

    @Override
    public boolean isThereAnyProductForThisWarehouse(Long warehouseId) {
        return warehouseOperationRepository.isThereAnyProductForThisWarehouse(warehouseId);
    }


    @Override
    public boolean isThereAnyActiveEntryAtThisId(Long id) {
        return warehouseOperationRepository.isThereAnyActiveEntryAtThisId(id);
    }

    @Override
    public WarehouseAPIResponseHolder<?> transferAllProducts(Long fromWarehouseId, Long toWarehouseId) {

        boolean isFromWarehouseExist = isThereAnyActiveEntryAtThisId(fromWarehouseId);
        boolean isToWarehouseExist = isThereAnyActiveEntryAtThisId(toWarehouseId);

        if (!isFromWarehouseExist || !isToWarehouseExist) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_WAREHOUSE_ID")
                    .message("An active repository for these ids was not found or selected warehouses are not active. Please check the values.")
                    .build());
        }

        // ürünleri diğer depoya aktarılacak olan deponun tüm ürünlerinin product warehouse daki bilgilerini alacağız
        Collection<ProductWarehouseEntity> stockCollection = getStocksFromThisWarehouse(fromWarehouseId);

        // bu collectiondaki product_id' lerle ve ürünlerin yeni ekleneceği warehouse id' lerle işleme devam et
        // bahsi geçen product id ve stoğun aktarılacağı warehouse id' nin veri tabanında kaydı var mı diye kontrol et
        // kaydı varsa demek ki o product o warehouse' da stoğu olan bir üründür o yüzden eski warehouse' daki ürünleri bu stoğun üstüne ekle
        // kaydı yoksa demek ki o product o warehouse' da stoğu olan bir ürün değildir o zaman yeni kayıt oluştur ve stok miktarını set et
        for (ProductWarehouseEntity productWarehouseEntity : stockCollection) {
            Long productId = productWarehouseEntity.getProductWarehouseId().getProductId();

            boolean isThereAnEntry = doesThisWarehouseHaveStockOfThisProduct(productId, toWarehouseId);

            Long stockAmount = productWarehouseEntity.getStockAmount();

            // TODO -  USER ID SİSTEME GİRİŞ YAPAN KULLANICININ İD' Sİ OLACAK. ŞİMDİLİK 1 VERİLDİ
            Long userId = 1L;

            if (isThereAnEntry) { // kayıt var yani stoğu üzerine ekleyeceğiz
                addThisStockToTheExistingRecord(productId, toWarehouseId, stockAmount, new Date(), userId);
            } else {
                addThisStockAsNewRecord(productId, toWarehouseId, stockAmount, new Date(), userId);
            }
        }

        // daha sonra bu collectiondaki tüm ProductWarehouse kayıtlarını vertabanından sil
        for (ProductWarehouseEntity productWarehouseEntity : stockCollection) {
            Long warehouseId = productWarehouseEntity.getProductWarehouseId().getWarehouseId();
            Long productId = productWarehouseEntity.getProductWarehouseId().getProductId();

            deleteEntryWithTheseIds(productId, warehouseId);
        }

        return new WarehouseAPIResponseHolder<>(HttpStatus.OK);
    }

    @Override
    public Collection<ProductWarehouseEntity> getStocksFromThisWarehouse(Long warehouseId) {
        return warehouseOperationRepository.getStocksFromThisWarehouse(warehouseId);
    }

    @Override
    public WarehouseAPIResponseHolder<Collection<Summary>> getProductsByWarehouseId(Long warehouseId) {

        boolean isWarehouseExist = isThereAnyActiveEntryAtThisId(warehouseId);

        if (!isWarehouseExist) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_WAREHOUSE_ID")
                    .message("An active repository for these ids was not found or selected warehouses are not active. Please check the values.")
                    .build());
        }

        Collection<Summary> warehouseProducts = warehouseOperationRepository.getProductsByWarehouseId(warehouseId);

        if (CollectionUtils.isEmpty(warehouseProducts)) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No records found in the database.")
                    .build());
        }

        return new WarehouseAPIResponseHolder<>(warehouseProducts,HttpStatus.OK);
    }

    @Override
    public WarehouseAPIResponseHolder<WarehouseSummary> getSummaryOfThisWarehouse(Long warehouseId) {



        WarehouseSummary summaryOfThisWarehouse = warehouseOperationRepository.getSummaryOfThisWarehouse(warehouseId);


        return new WarehouseAPIResponseHolder<>(summaryOfThisWarehouse,HttpStatus.OK);
    }

    @Override
    public void deleteEntryWithTheseIds(Long productId, Long warehouseId) {
        warehouseOperationRepository.deleteEntryWithTheseIds(productId, warehouseId);
    }

    @Override
    public boolean doesThisWarehouseHaveStockOfThisProduct(Long productId, Long warehouseId) {
        return warehouseOperationRepository.doesThisWarehouseHaveStockOfThisProduct(productId, warehouseId);
    }

    @Override
    public void addThisStockToTheExistingRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {
        warehouseOperationRepository.addThisStockToTheExistingRecord(productId, warehouseId, stockAmount, date, userId);
    }

    @Override
    public void addThisStockAsNewRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {
        warehouseOperationRepository.addThisStockAsNewRecord(productId, warehouseId, stockAmount, date, userId);
    }

    /*@Override
    public void addThisStockAsNewRecord(ProductWarehouseEntity productWarehouseEntity) {
        warehouseOperationRepository.addThisStockAsNewRecord(productWarehouseEntity);
    }*/
}