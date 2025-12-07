package com.ptmhdv.SellPhone.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_details")
@Data
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
}
