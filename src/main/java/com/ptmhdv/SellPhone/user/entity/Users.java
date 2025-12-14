package com.ptmhdv.SellPhone.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.order.entity.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "`Users`")
@Data
public class Users {

    @Id
    @Column(name = "user_id", length = 36) // Sửa để khớp với DB (user_id)
    private String userId;

    @PrePersist
    public void generateId() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID().toString();
        }
    }

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email
    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "full_name", length = 150) // Sửa để khớp với DB (fullname)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id") // Sửa để khớp với DB (role_id)
    private Roles role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Orders> orders;

    public List<CartItem> getCartItems() {
        if (this.cart == null) {
            return List.of();
        }
        return this.cart.getCartItems();
    }
}