package com.ptmhdv.SellPhone.cart.repository;

import com.ptmhdv.SellPhone.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCart_CartId(String cartId);

    void deleteByCart_CartIdAndPhone_PhoneId(String cartId, String phoneId);

    Optional<CartItem> findByCartCartIdAndPhonePhoneId(String cartId, String phoneId);

    void deleteByCart_CartId(String cartId);

    // ✅ IMPORTANT: chỉ lấy các item thuộc đúng cartId + nằm trong list ids
    @Query("""
        select ci from CartItem ci
        where ci.cart.cartId = :cartId
          and ci.cartItemId in :ids
    """)
    List<CartItem> findByCartIdAndCartItemIdIn(
            @Param("cartId") String cartId,
            @Param("ids") Collection<String> ids
    );
}
