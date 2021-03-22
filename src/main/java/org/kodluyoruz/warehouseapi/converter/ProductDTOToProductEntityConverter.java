package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class ProductDTOToProductEntityConverter implements WarehouseAPIConverter<ProductDTO, ProductEntity> {
    @Override
    public ProductEntity convert(ProductDTO input) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(input.getId());
        productEntity.setCode(input.getCode());
        productEntity.setName(input.getName());
        productEntity.setPrice(input.getPrice());
        productEntity.setStatus(input.getStatus());
        productEntity.setVatRate(input.getVatRate());
        productEntity.setVatAmount(input.getVatAmount());
        productEntity.setVatIncludedPrice(input.getVatIncludedPrice());
        productEntity.setCreatedAt(Objects.isNull(input.getCreatedAt()) ? new Date() : input.getCreatedAt());
        productEntity.setUpdatedAt(input.getUpdatedAt());

        return productEntity;
    }
}