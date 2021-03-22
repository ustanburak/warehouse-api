package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseError;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.converter.ProductWarehouseDTOToProductWarehouseEntityConverter;
import org.kodluyoruz.warehouseapi.converter.ProductWarehouseEntityToProductWarehouseDTOConverter;
import org.kodluyoruz.warehouseapi.dao.StockOperationRepository;
import org.kodluyoruz.warehouseapi.model.dto.ProductWarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.kodluyoruz.warehouseapi.model.entites.Summary;
import org.kodluyoruz.warehouseapi.service.ProductsOperationService;
import org.kodluyoruz.warehouseapi.service.StockOperationService;
import org.kodluyoruz.warehouseapi.service.WarehouseOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class StockOperationServiceImpl implements StockOperationService {

    private final StockOperationRepository stockOperationRepository;
    private final WarehouseOperationService warehouseOperationService;
    private final ProductsOperationService productsOperationService;
    private final ProductWarehouseDTOToProductWarehouseEntityConverter productWarehouseDTOToProductWarehouseEntityConverter;
    private final ProductWarehouseEntityToProductWarehouseDTOConverter productWarehouseEntityToProductWarehouseDTOConverter;

    @Override
    public WarehouseAPIResponseHolder<?> transferThisProduct(Long productId, Long fromWarehouseId, Long toWarehouseId) {

        boolean isProductActive = productsOperationService.isThereAnyActiveEntryAtThisId(productId);
        boolean isFromWarehouseActive = warehouseOperationService.isThereAnyActiveEntryAtThisId(fromWarehouseId);
        boolean isToWarehouseActive = warehouseOperationService.isThereAnyActiveEntryAtThisId(toWarehouseId);

        // herhangi biri aktif değilse işlem yapılamaz
        if (!isProductActive || !isFromWarehouseActive || !isToWarehouseActive) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("SOMETHING_IS_WRONG")
                    .message("There may not be a entry of the ids sent or these entries may not be active.")
                    .build());
        }



        // aktarılacak ürünün stok miktarı
        Long stockAmount = getStockOfGivenProductFromThisWarehouse(productId, fromWarehouseId);

        // aktarılacak ürün stoğu yoksa hata ver
        if (stockAmount == null){
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NOT_IN_STOCK")
                    .message("The specified warehouse does not have such a product. You cannot transfer a product that is not in stock of the warehouse.")
                    .build());
        }

        // aktarılacak ürün yeni warehouse' da var mı? varsa üzerine ekleyeceğiz. yoksa yeni kayıt olarak gireceğiz
        boolean isThereaEntry = doesThisWarehouseHaveStockOfThisProduct(productId, toWarehouseId);

        // TODO - BU USER ID SİSTEME GİRİŞ YAPAN KULLANICININ İD Sİ OLACAK. ŞİMDİLİK HEPSİNE 1 VERİLDİ
        Long userId = 1L;

        if (isThereaEntry) { // kayıt var yani stoğu üzerine ekleyeceğiz
            addThisStockToTheExistingRecord(productId, toWarehouseId, stockAmount, new Date(), userId);
        } else {
            addThisStockAsNewRecord(productId, toWarehouseId, stockAmount, new Date(), userId);
        }

        // eski kaydı siliyoruz
        deleteEntryWithTheseIds(productId, fromWarehouseId);

        return new WarehouseAPIResponseHolder<>(HttpStatus.OK);
    }


    @Override
    public WarehouseAPIResponseHolder<?> updateProductStockQuantity(ProductWarehouseDTO productWarehouseDTO) {

        // ilgili ürün product tablosunda var mı ve aktif mi?
        boolean isThereaActiveProductWithThisId = productsOperationService.isThereAnyActiveEntryAtThisId(productWarehouseDTO.getProductWarehouseId().getProductId());

        // ilgili ürün product tablosunda yoksa veya aktif dğeilse hata fırlat
        if (!isThereaActiveProductWithThisId) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_PRODUCT")
                    .message("A product with this id was not found or product is not active.")
                    .build());
        }

        // ilgili warehouse, warehosue tablosunda var mı ve aktif mi?
        boolean isThereaActiveWarehouseWithThisId = warehouseOperationService.isThereAnyActiveEntryAtThisId(productWarehouseDTO.getProductWarehouseId().getWarehouseId());

        // ilgili warehouse, warehouse tablosunda yoksa veya aktif dğeilse hata fırlat
        if (!isThereaActiveWarehouseWithThisId) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_PRODUCT")
                    .message("A warehouse with this id was not found or warehouse is not active.")
                    .build());
        }

        // ilgili ürün ilgili depoda var mı
        boolean isThereAnEntry = doesThisWarehouseHaveStockOfThisProduct(productWarehouseDTO.getProductWarehouseId().getProductId(),
                productWarehouseDTO.getProductWarehouseId().getWarehouseId());

        // ilgili ürün ilgili depoda yoksa hata fırlat
        if (!isThereAnEntry) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_ENTRY")
                    .message("There is no such product in stock of this warehouse.Please check the values.")
                    .build());
        }

        // Ürüne ait yeni stock miktarı 0'dan küçük olmamalı.
        if (productWarehouseDTO.getStockAmount() < 0) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_VALUE")
                    .message("New product stock quantity cannot be less than zero.")
                    .build());
        }

        // değerleri entity olarak göndererek güncellemek
        ProductWarehouseEntity productWarehouseEntity = productWarehouseDTOToProductWarehouseEntityConverter.convert(productWarehouseDTO);
        ProductWarehouseEntity updatedEntity = stockOperationRepository.updateProductStockQuantity(productWarehouseEntity);
        return new WarehouseAPIResponseHolder<>(productWarehouseEntityToProductWarehouseDTOConverter.convert(updatedEntity),
                HttpStatus.OK);

        // değerleri tek tek göndererek güncellemek
        /*stockOperationRepository.updateProductStockQuantity(productWarehouseDTO.getProductWarehouseId().getProductId(), productWarehouseDTO.getProductWarehouseId().getWarehouseId(),
                productWarehouseDTO.getStockAmount(), new Date(), productWarehouseDTO.getCreatedBy());
        return new WarehouseAPIResponseHolder<>(HttpStatus.OK);*/
    }

    @Override
    public Long getStockOfGivenProductFromThisWarehouse(Long productId, Long warehouseId) {
        return stockOperationRepository.getStockOfGivenProductFromThisWarehouse(productId, warehouseId);
    }

    @Override
    public boolean doesThisWarehouseHaveStockOfThisProduct(Long productId, Long warehouseId) {
        return stockOperationRepository.doesThisWarehouseHaveStockOfThisProduct(productId, warehouseId);
    }

    @Override
    public void addThisStockToTheExistingRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {
        stockOperationRepository.addThisStockToTheExistingRecord(productId, warehouseId, stockAmount, date, userId);
    }

    @Override
    public void addThisStockAsNewRecord(Long productId, Long warehouseId, Long stockAmount, Date date, Long userId) {
        stockOperationRepository.addThisStockAsNewRecord(productId, warehouseId, stockAmount, date, userId);
    }

    @Override
    public void deleteEntryWithTheseIds(Long productId, Long warehouseId) {
        stockOperationRepository.deleteEntryWithTheseIds(productId, warehouseId);
    }

    @Override
    public WarehouseAPIResponseHolder<Collection<Summary>> getSummaries() {
        Collection<Summary> summaries = stockOperationRepository.getSummaries();

        if (CollectionUtils.isEmpty(summaries)) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No records found in the database.")
                    .build());
        }
        return new WarehouseAPIResponseHolder<>(summaries, HttpStatus.OK);
    }

    @Override
    public WarehouseAPIResponseHolder<?> addNewStock(ProductWarehouseDTO productWarehouseDTO) {

        // ilgili ürün product tablosunda var mı ve aktif mi?
        boolean isThereaActiveProductWithThisId = productsOperationService.isThereAnyActiveEntryAtThisId(productWarehouseDTO.getProductWarehouseId().getProductId());

        // ilgili ürün product tablosunda yoksa veya aktif dğeilse hata fırlat
        if (!isThereaActiveProductWithThisId) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_PRODUCT")
                    .message("A product with this id was not found or product is not active. You cannot add an inactive product to stock.")
                    .build());
        }

        // ilgili warehouse, warehosue tablosunda var mı ve aktif mi?
        boolean isThereaActiveWarehouseWithThisId = warehouseOperationService.isThereAnyActiveEntryAtThisId(productWarehouseDTO.getProductWarehouseId().getWarehouseId());

        // ilgili warehouse, warehouse tablosunda yoksa veya aktif dğeilse hata fırlat
        if (!isThereaActiveWarehouseWithThisId) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_PRODUCT")
                    .message("A warehouse with this id was not found or warehouse is not active.You cannot add stock to an inactive warehouse.")
                    .build());
        }

        // ilgili ürün ilgili depoda var mı
        boolean isThereAnEntry = doesThisWarehouseHaveStockOfThisProduct(productWarehouseDTO.getProductWarehouseId().getProductId(),
                productWarehouseDTO.getProductWarehouseId().getWarehouseId());

        // ilgili ürün ilgili depoda varsa bu yeni bir ürün sotğu girilme işlemi değil güncelleme işlemidir. güncelleme üzerinden yapılmalıdır
        if (isThereAnEntry) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("DUPLICATE_DATA")
                    .message("This product is already in stock at this warehouse. You cannot add a stock of an added product as a new record. " +
                            "If you want to update the stock quantity, please use the update process.")
                    .build());
        }

        // Ürüne ait yeni stock miktarı 0'dan küçük olmamalı.
        if (productWarehouseDTO.getStockAmount() < 0) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_VALUE")
                    .message("Product stock quantity cannot be less than zero.")
                    .build());
        }


        ProductWarehouseEntity productWarehouseEntity = productWarehouseDTOToProductWarehouseEntityConverter.convert(productWarehouseDTO);
        stockOperationRepository.addThisStockAsNewRecord(productWarehouseEntity.getProductWarehouseId().getProductId(),
                productWarehouseEntity.getProductWarehouseId().getWarehouseId(),productWarehouseEntity.getStockAmount(),
                new Date(),productWarehouseEntity.getCreatedBy());
        return new WarehouseAPIResponseHolder<>(HttpStatus.OK);
    }

}