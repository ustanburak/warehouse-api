package org.kodluyoruz.warehouseapi.dao;


import org.kodluyoruz.warehouseapi.model.entites.ProductEntity;

import java.util.Collection;

public interface ProductCRUDRepository {
    Collection<ProductEntity> list();

    ProductEntity create(ProductEntity productEntity);

    ProductEntity update(ProductEntity productEntity);

    void delete(Long id);
}
