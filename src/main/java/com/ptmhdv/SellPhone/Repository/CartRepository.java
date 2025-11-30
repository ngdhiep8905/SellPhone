package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Cart;
import com.ptmhdv.SellPhone.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(Users user);
}
