package org.kodluyoruz.warehouseapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;
import org.kodluyoruz.warehouseapi.base.WarehouseAPIResponseHolder;
import org.kodluyoruz.warehouseapi.model.dto.ProductDTO;
import org.kodluyoruz.warehouseapi.service.ProductCRUDService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class ProductCRUDServiceImpl implements ProductCRUDService {

    @Override
    public WarehouseAPIResponseHolder<Collection<ProductDTO>> list() {
        return null;
    }

    @Override
    public WarehouseAPIResponseHolder<ProductDTO> create(ProductDTO data) {
        return null;
    }

    @Override
    public WarehouseAPIResponseHolder<ProductDTO> update(ProductDTO data) {
        return null;
    }

    @Override
    public WarehouseAPIResponseHolder<?> delete(BaseIDDTO data) {
        return null;
    }
}
