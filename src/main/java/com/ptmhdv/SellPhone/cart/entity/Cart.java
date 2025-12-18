package com.ptmhdv.SellPhone.cart.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptmhdv.SellPhone.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "cart")
@Data
public class Cart {

    @Id
    @Column(name = "cart_id", length = 6)
    private String cartId;

    @PrePersist
    public void generateId() {
        if (cartId == null) {
            cartId = "C" + String.format("%05d", System.currentTimeMillis() % 100000);
        }
    }

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items;

    public List<CartItem> getItems() {
        return items == null ? List.of() : items;
    }

}
