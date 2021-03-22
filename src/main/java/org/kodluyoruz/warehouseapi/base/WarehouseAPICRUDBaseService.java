package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface WarehouseAPICRUDBaseService<T extends BaseIDDTO> {

    WarehouseAPIResponseHolder<Collection<T>> list();

    WarehouseAPIResponseHolder<T> getById(Long id);

    ResponseEntity<WarehouseAPIResponseHolder<T>> create(T data);

    ResponseEntity<WarehouseAPIResponseHolder<T>> update(Long id, T data);

    ResponseEntity<WarehouseAPIResponseHolder<?>> delete(Long id);
}
