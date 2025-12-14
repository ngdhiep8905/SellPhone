package com.ptmhdv.SellPhone.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.order.entity.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "`Users`")
@Data
public class Users {

    @Id
    @Column(name = "userId", length = 36)
    private String userId;

    @PrePersist
    public void generateId() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID().toString();
        }
    }

    @NotBlank
    @Column(unique = true, nullable = false, length = 100)
    private String userName;

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

    @Column(length = 150)
    private String fullName;

    @Column(length = 20)
    private String status = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Roles role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Orders> orders;

    public List<CartItem> getCartItems() {
        if (this.cart == null) {
            return List.of(); // Tránh NullPointerException
        }
        return this.cart.getCartItems();
    }

}


