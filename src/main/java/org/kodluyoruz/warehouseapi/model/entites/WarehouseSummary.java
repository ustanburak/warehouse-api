package org.kodluyoruz.warehouseapi.model.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSummary {
    private Long numberOfProductTypes;
    private Long totalStockQuantityOfAllProducts;
   /* private BigDecimal mostExpensiveProductPrice;
    private BigDecimal cheapestProductPrice;
    private BigDecimal totalValueOfAllProducts;*/
}