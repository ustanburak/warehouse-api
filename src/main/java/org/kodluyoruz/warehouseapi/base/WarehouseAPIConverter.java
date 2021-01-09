package org.kodluyoruz.warehouseapi.base;


public interface WarehouseAPIConverter<T, R> {

    R convert(T input);
}
