package org.kodluyoruz.warehouseapi.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;


@Getter
@Setter
public class WarehouseDTO extends BaseIDDTO {
    private String name;
    private String code;
    private StatusEnum status;
}