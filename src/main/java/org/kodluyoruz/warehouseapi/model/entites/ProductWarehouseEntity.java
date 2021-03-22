package org.kodluyoruz.warehouseapi.model.entites;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "product_warehouse")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PRODUCT_WAREHOUSE")
public class ProductWarehouseEntity {

    @EmbeddedId
    private ProductWarehouseId productWarehouseId;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("warehouseId")
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouseEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @Column(name = "STOCK_AMOUNT", precision = 19, scale = 2, nullable = false)
    private Long stockAmount = Long.valueOf(0);

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TRANSACTION_DATE", nullable = false, updatable = false)
    private Date createdAt;

    @Column(name = "CREATED_BY", precision = 19, scale = 2, nullable = false)
    private Long createdBy;

}