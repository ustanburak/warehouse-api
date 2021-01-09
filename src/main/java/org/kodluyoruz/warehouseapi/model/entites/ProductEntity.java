package org.kodluyoruz.warehouseapi.model.entites;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.ProductStatus;
import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PRODUCT")
public class ProductEntity extends BaseEntity {

    @Column(name = "CODE", unique = true, length = 50, nullable = false)
    private String code;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "VAT_RATE", precision = 3, scale = 2, nullable = false)
    private BigDecimal vatRate = BigDecimal.valueOf(00.00);

    @Column(name = "VAT_AMOUNT", precision = 19, scale = 2, nullable = false)
    private BigDecimal vatAmount = BigDecimal.valueOf(00.00);

    @Column(name = "PRICE", precision = 19, scale = 2, nullable = false)
    private BigDecimal price = BigDecimal.valueOf(00.00);

    @Column(name = "VAT_INCLUDED_PRICE", precision = 19, scale = 2, nullable = false)
    private BigDecimal vatIncludedPrice = BigDecimal.valueOf(00.00);

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS", length = 7, nullable = false)

    private ProductStatus status = ProductStatus.ACTIVE;



}
