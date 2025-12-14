package com.ptmhdv.SellPhone.cart.repository;

import com.ptmhdv.SellPhone.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCart_CartId(String cartId);

    void deleteByCart_CartIdAndPhone_PhoneId(String cartId, String phoneId);

    Optional<CartItem> findByCartCartIdAndPhonePhoneId(String cartId, String phoneId);
}
