package com.ptmhdv.SellPhone.cart.repository;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

    Cart findByUser_UserId(String userId);

    Optional<Cart> findByUserUserId(String userId);
}
