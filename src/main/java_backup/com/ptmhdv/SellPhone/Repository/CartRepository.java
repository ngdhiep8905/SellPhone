package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {

    Cart findByUser_UserId(String userId);
}
