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
    @Column(name = "cart_id", length = 50)
    private String cartId;

    /**
     * Token dùng cho guest cart (không đăng nhập).
     * FE giữ token này trong cookie/localStorage và gửi lên để thao tác giỏ.
     */
    @Column(name = "cart_token", nullable = false, unique = true)
    private String cartToken;

    @PrePersist
    public void generateIdAndToken() {
        if (cartId == null) {
            cartId = "C" + java.util.UUID.randomUUID().toString().replace("-", "");

        }
        if (cartToken == null || cartToken.isBlank()) {
            cartToken = UUID.randomUUID().toString();
        }
    }


    @OneToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> items = new java.util.ArrayList<>();


    public List<CartItem> getItems() {
        return items == null ? List.of() : items;
    }
}
