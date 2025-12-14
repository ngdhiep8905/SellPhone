package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCart_CartId(String cartId);

    void deleteByCart_CartIdAndPhone_PhoneId(String cartId, String phoneId);
}
