package org.kodluyoruz.warehouseapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.entites.ProductWarehouseId;

import java.util.Date;

@Getter
@Setter
public class ProductWarehouseDTO {

    private ProductWarehouseId productWarehouseId;

    private Long stockAmount;

    private Date createdAt;

    private Long createdBy;
}