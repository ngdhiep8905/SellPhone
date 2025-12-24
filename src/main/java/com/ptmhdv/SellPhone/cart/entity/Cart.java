package com.ptmhdv.SellPhone.cart.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptmhdv.SellPhone.user.entity.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "cart",
        indexes = {
                @Index(name = "idx_cart_token", columnList = "cart_token"),
                @Index(name = "idx_cart_user_id", columnList = "user_id")
        }
)
@Data
public class Cart {

    @Id
    @Column(name = "cart_id", length = 36)
    private String cartId;

    @Column(name = "cart_token", nullable = false, unique = true, length = 36)
    private String cartToken;

    @PrePersist
    public void generateIdAndToken() {
        if (cartId == null || cartId.isBlank()) {
            cartId = UUID.randomUUID().toString(); // ✅ 36 chars, match DB CHAR(36)
        }
        if (cartToken == null || cartToken.isBlank()) {
            cartToken = UUID.randomUUID().toString();
        }
    }

    // DB cho phép nhiều cart cùng trỏ 1 user? Thực tế bạn muốn 1-1 thì giữ OneToOne cũng được.
    // Nhưng phổ biến & an toàn hơn là ManyToOne (vì DB không enforce UNIQUE(user_id)).
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new java.util.ArrayList<>();

    public List<CartItem> getItems() {
        return items == null ? List.of() : items;
    }
}
