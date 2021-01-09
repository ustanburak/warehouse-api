package org.kodluyoruz.warehouseapi.controller;


import lombok.RequiredArgsConstructor;
import org.kodluyoruz.warehouseapi.config.SwaggerClient;
import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.service.ProductCRUDService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@SwaggerClient
@RequestMapping("/products")
public class ProductCRUDController {

    private final ProductCRUDService productCRUDService;

    @GetMapping
    public WarehouseAPIResponseHolder<Collection<ProductDTO>> getProducts() {
        return productCRUDService.list();
    }

    @PostMapping()
    public WarehouseAPIResponseHolder<ProductDTO> create(ProductDTO productDTO) {
        return productCRUDService.create(productDTO);
    }

    @PutMapping
    public WarehouseAPIResponseHolder<ProductDTO> update(ProductDTO productDTO) {
        return productCRUDService.update(productDTO);
    }

    @DeleteMapping
    public WarehouseAPIResponseHolder<?> update(BaseIDDTO id) {
        return productCRUDService.delete(id);
    }
}
