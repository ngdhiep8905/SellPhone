package com.ptmhdv.SellPhone.order.entity;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Data
@Entity
@Table(name = "order_details")


public class OrdersPhones {

    @Id
    @Column(name = "order_detail_id", length = 36)
    private String orderDetailId;

    @PrePersist
    public void generateId() {
        if (orderDetailId == null) {
            orderDetailId = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "phone_id", nullable = false)
    private Phones phone;


    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;





}
