package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.ProductWarehouseDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProductWarehouseEntityToProductWarehouseDTOConverter implements
        WarehouseAPIConverter<ProductWarehouseEntity, ProductWarehouseDTO> {
    @Override
    public ProductWarehouseDTO convert(ProductWarehouseEntity input) {
        ProductWarehouseDTO productWarehouseDTO = new ProductWarehouseDTO();
        productWarehouseDTO.setProductWarehouseId(input.getProductWarehouseId());
        productWarehouseDTO.setStockAmount(input.getStockAmount());
        productWarehouseDTO.setCreatedAt(new Date());
        productWarehouseDTO.setCreatedBy(input.getCreatedBy());

        return productWarehouseDTO;
    }
}