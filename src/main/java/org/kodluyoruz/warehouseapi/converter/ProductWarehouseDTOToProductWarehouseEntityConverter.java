package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.ProductWarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProductWarehouseDTOToProductWarehouseEntityConverter implements WarehouseAPIConverter<ProductWarehouseDTO,
        ProductWarehouseEntity> {
    @Override
    public ProductWarehouseEntity convert(ProductWarehouseDTO input) {
        ProductWarehouseEntity productWarehouseEntity = new ProductWarehouseEntity();
        productWarehouseEntity.setProductWarehouseId(input.getProductWarehouseId());
        productWarehouseEntity.setStockAmount(input.getStockAmount());
        productWarehouseEntity.setCreatedAt(new Date());
        productWarehouseEntity.setCreatedBy(input.getCreatedBy());
        return productWarehouseEntity;
    }
}