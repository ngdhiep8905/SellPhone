package com.ptmhdv.sellphone.cart.entity;

import com.ptmhdv.sellphone.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart")
@Data
public class Cart {

    @Id
    @Column(name = "cart_id", length = 36)
    private String cartId;

    @PrePersist
    public void generateId() {
        if (cartId == null) {
            cartId = UUID.randomUUID().toString();
        }
    }

    // Mỗi user chỉ có 1 cart
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    // Danh sách sản phẩm trong giỏ
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    public Cart() {}

    public Cart(Users user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return this.items != null ? this.items : List.of();
    }

}
