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
@Table(name = "users")
@Data
public class Users {

    @Id
    @Column(name = "user_id", length = 6)
    private String userId;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Email
    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Orders> orders;
    @JsonIgnore
    public List<CartItem> getCartItems() {
        if (this.cart == null) {
            return List.of();
        }
        return this.cart.getItems();
    }
}
