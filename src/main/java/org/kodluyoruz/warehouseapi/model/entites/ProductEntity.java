package org.kodluyoruz.warehouseapi.model.entites;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.warehouseapi.model.enums.StatusEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;


@Entity(name = "product")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PRODUCT")
public class ProductEntity extends BaseEntity {

    @OneToMany(mappedBy = "productEntity",cascade = CascadeType.ALL)
    Set<ProductWarehouseEntity> productWarehouseEntitySet;

    @Column(name = "CODE", unique = true, length = 50, nullable = false)
    @NotNull(message = "{@NotNull.code})")
    private String code;

    @Column(name = "NAME", length = 100, nullable = false)
    @NotNull(message = "{@NotNull.name})")
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
    private StatusEnum status = StatusEnum.ACTIVE;

    @JsonBackReference
    @OneToMany(mappedBy = "productEntity")
    private Collection<ProductWarehouseEntity> productWarehouseEntities;
}