package org.kodluyoruz.warehouseapi.converter;

import org.kodluyoruz.warehouseapi.base.WarehouseAPIConverter;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityToProductDTOConverter implements WarehouseAPIConverter<ProductEntity, ProductDTO> {
    @Override
    public ProductDTO convert(ProductEntity input) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(input.getId());
        productDTO.setName(input.getName());
        productDTO.setCode(input.getCode());
        productDTO.setVatRate(input.getVatRate());
        productDTO.setVatAmount(input.getVatAmount());
        productDTO.setPrice(input.getPrice());
        productDTO.setVatIncludedPrice(input.getVatIncludedPrice());
        productDTO.setStatus(input.getStatus());
        productDTO.setCreatedAt(input.getCreatedAt());
        productDTO.setUpdatedAt(input.getUpdatedAt());

        return productDTO;
    }
}
