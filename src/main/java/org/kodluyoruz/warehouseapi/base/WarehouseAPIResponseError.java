package org.kodluyoruz.warehouseapi.base;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WarehouseAPIResponseError {

    private String code;
    private String message;


}
