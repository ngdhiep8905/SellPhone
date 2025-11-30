package com.ptmhdv.SellPhone.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="CartItem")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItemId")
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "phoneId")
    private Phones phone;

    @NotNull(message = "Quantity Price is required")
    @Min(value = 1, message = "Quantity Price must be at least 1")
    @Column(name = "quantityPrice")
    private int quantityPrice;

}
