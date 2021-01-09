package org.kodluyoruz.warehouseapi.base;

import org.kodluyoruz.warehouseapi.model.dto.BaseIDDTO;

import java.util.Collection;

public interface WarehouseAPICRUDBaseService<T extends BaseIDDTO> {

    WarehouseAPIResponseHolder<Collection<T>> list();

    WarehouseAPIResponseHolder<T> create(T data);

    WarehouseAPIResponseHolder<T> update(T data);

    WarehouseAPIResponseHolder<?> delete(BaseIDDTO data);
}
