package com.ptmhdv.SellPhone.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "phone_id"})
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Integer cartItemId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private Cart cart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phone_id", nullable = false)
    private Phones phone;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Transient
    public BigDecimal getQuantityPrice() {
        if (phone == null || phone.getPrice() == null) return BigDecimal.ZERO;
        return phone.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
