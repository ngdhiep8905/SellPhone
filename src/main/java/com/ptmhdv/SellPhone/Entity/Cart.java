package com.ptmhdv.SellPhone.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name="Cart")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartId")
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "userId", unique = true)
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    public Cart(Long cartId, Users user, List<CartItem> items) {
        this.cartId = cartId;
        this.user = user;
        this.items = items;
    }

    public Cart() {
    }
}
