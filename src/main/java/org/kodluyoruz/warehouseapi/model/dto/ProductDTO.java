package org.kodluyoruz.warehouseapi.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductDTO extends BaseIDDTO {

    private String code;
    private String name;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal price;
    private BigDecimal vatIncludedPrice;
    private ProductStatus status;
}
