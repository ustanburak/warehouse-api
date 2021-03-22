package org.kodluyoruz.warehouseapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseError;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.converter.ProductDTOToProductEntityConverter;
import org.kodluyoruz.warehouseapi.converter.ProductEntityToProductDTOConverter;
import org.kodluyoruz.warehouseapi.dao.ProductCRUDRepository;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.kodluyoruz.warehouseapi.service.ProductCRUDService;
import org.kodluyoruz.warehouseapi.service.ProductsOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCRUDServiceImpl implements ProductCRUDService {

    private final ProductCRUDRepository productCRUDRepository;
    private final ProductsOperationService productsOperationService;
    private final ProductEntityToProductDTOConverter productEntityToProductDTOConverter;
    private final ProductDTOToProductEntityConverter productDTOToProductEntityConverter;

    @Override
    public WarehouseAPIResponseHolder<Collection<ProductDTO>> list() {
        Collection<ProductEntity> productEntities = productCRUDRepository.list();
        if (CollectionUtils.isEmpty(productEntities)) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND);
        }
        List<ProductDTO> productDTOList = productEntities
                .stream()
                .map(productEntityToProductDTOConverter::convert)
                .collect(Collectors.toList());
        return new WarehouseAPIResponseHolder<>(productDTOList, HttpStatus.OK);
    }

    @Override
    public WarehouseAPIResponseHolder<ProductDTO> getById(Long id) {
        ProductEntity productEntity = productCRUDRepository.getById(id);
        if (productEntity == null) {
            return new WarehouseAPIResponseHolder<>(HttpStatus.NOT_FOUND, WarehouseAPIResponseError
                    .builder()
                    .code("DATA_NOT_FOUND")
                    .message("No record found in the database.")
                    .build());
        }
        ProductDTO productDTO = productEntityToProductDTOConverter.convert(productEntity);
        return new WarehouseAPIResponseHolder<>(productDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<ProductDTO>> create(ProductDTO data) {
        if (Objects.isNull(data)) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT));
        }

        String productName = data.getName();
        String productStatus = data.getStatus().toString();
        String productCode = data.getCode();
        String productPrice = data.getPrice().toString();
        String productVatAmount = data.getVatAmount().toString();
        String productVatIncludedPrice = data.getVatIncludedPrice().toString();
        String productVatRate = data.getVatRate().toString();

        // herhangi bir alan boş geçilmişse hata fırlatılmalı
        if (productName.isEmpty() || productStatus.isEmpty() || productCode.isEmpty() || productPrice.isEmpty() ||
                productVatAmount.isEmpty() || productVatIncludedPrice.isEmpty() || productVatRate.isEmpty()) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("EMPTY_DATA")
                    .message("No field can be left empty. Please fill in all fields.")
                    .build()));
        }

        // Aynı ürün kodu ile birden fazla ürün olmamalı, girilen ürün kodu sistemde mevcutsa hata fırlatılmalı.
        boolean isExist = productsOperationService.hasExistSameCode(data.getCode());
        if (isExist) {

            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("DUPLICATE_DATA")
                    .message("You can not insert with same Product Code")
                    .build()));
        }

        // Ürün fiyatı, kdv oranı, kdv' si veya kdv' li ürün fiyatı negatif olamaz.
        if (Integer.parseInt(productPrice) < 0 || Integer.parseInt(productVatAmount) < 0 || Integer.parseInt(productVatIncludedPrice) < 0
                || Integer.parseInt(productVatRate) < 0) {
            return ResponseEntity.badRequest().body( new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("WRONG_DATA")
                    .message("Product price, VAT rate, VAT or product price with VAT cannot be negative.")
                    .build()));
        }

        ProductEntity productEntity = productDTOToProductEntityConverter.convert(data);
        productCRUDRepository.create(productEntity);

        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(productEntityToProductDTOConverter
                .convert(productEntity), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<ProductDTO>> update(Long id, ProductDTO data) {

        data.setId(id);
        // DB'de kayıtlı bir ürün yok ise hata fırlatılmalı
        boolean isAnyProductsExists = productsOperationService.isThereAnyOfThis();
        if (!isAnyProductsExists) {

            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.NO_CONTENT, WarehouseAPIResponseError
                    .builder()
                    .code("NO_DATA")
                    .message("Sorry,There is no warehouse.")
                    .build()));

        }

        // Status deleted işaretlenmişse depoda ürün varsa deleted işaretleme yapılmamalı
        String warehouseStatus = data.getStatus().toString();
        if (warehouseStatus.equals("DELETED")) {
            boolean thereAnyProductForThisId = productsOperationService.isThereAnyProductForThisIdInStock(data.getId());
            if (thereAnyProductForThisId) {
                return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                        .builder()
                        .code("THERE_IS_A_PRODUCT_HERE")
                        .message("Sorry, you cannot delete this item because this item is in stock.")
                        .build()));
            }
        }

        // güncelleme yaparken ürün codu değiştirilirse ve aynı kod ile kayıtlı başka bir ürün varsa kaydı güncelleme
        boolean isExist = productsOperationService.hasExistSameCodeAndId(data.getId(), data.getCode());
        if (isExist) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("DUPLICATE_DATA")
                    .message("Sorry, you can't update this record. There is another record with this code.")
                    .build()));
        }

        ProductEntity updateEntity = productDTOToProductEntityConverter.convert(data);
        updateEntity.setUpdatedAt(new Date());
        ProductEntity updatedEntity = productCRUDRepository.update(updateEntity);


        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(productEntityToProductDTOConverter.convert(updatedEntity),
                HttpStatus.OK));
    }

    @Override
    public ResponseEntity<WarehouseAPIResponseHolder<?>> delete(Long id) {
        // Ürün silinmeden önce mutlaka stok bilgisine bakılmalı.
        // Depolar içerisinde ilgili ürüne ait stoğu 0'dan büyük bir kayıt var ise ürün silinmemeli ve hata fırlatılmalı

        boolean thereAnyProductForThisId = productsOperationService.isThereAnyProductForThisIdInStock(id);
        if (thereAnyProductForThisId) {
            return ResponseEntity.badRequest().body(new WarehouseAPIResponseHolder<>(HttpStatus.BAD_REQUEST, WarehouseAPIResponseError
                    .builder()
                    .code("CANNOT_DELETE")
                    .message("Sorry you can't delete this item because this product is in stock.")
                    .build()));
        }

        productCRUDRepository.delete(id);
        return ResponseEntity.ok().body(new WarehouseAPIResponseHolder<>(HttpStatus.OK));
    }
}