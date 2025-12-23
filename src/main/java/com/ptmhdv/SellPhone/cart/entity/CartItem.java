package com.ptmhdv.SellPhone.cart.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "phone_id"})
)
public class CartItem {

    @Id
    @Column(name = "cart_item_id", length = 50)
    private String cartItemId;

    @PrePersist
    public void generateId() {
        if (cartItemId == null) cartItemId = UUID.randomUUID().toString();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_id", nullable = false)
    private Phones phone;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public BigDecimal getQuantityPrice() {
        if (phone == null || phone.getPrice() == null) return BigDecimal.ZERO;
        return phone.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

