package org.kodluyoruz.warehouseapi.exceptions;

public abstract class WarehouseAPIBaseException extends RuntimeException {

    protected WarehouseAPIBaseException(String message) {
        super(message);
    }
}
