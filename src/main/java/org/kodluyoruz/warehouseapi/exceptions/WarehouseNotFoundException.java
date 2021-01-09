package org.kodluyoruz.warehouseapi.exceptions;

public class WarehouseNotFoundException extends WarehouseAPIBaseException {

    private static final String MESSAGE = "warehouse.not.found";

    public WarehouseNotFoundException() {
        super(MESSAGE);
    }

    public WarehouseNotFoundException(String message) {
        super(message);
    }
}
