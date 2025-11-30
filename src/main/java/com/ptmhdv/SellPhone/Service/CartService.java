package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Cart;

public interface CartService {

    Cart getCartByUser(Long userId);

    Cart addToCart(Long userId, Long phoneId, Integer quantity);

    Cart updateQuantity(Long cartItemId, Integer quantity);

    Cart removeItem(Long cartItemId);
}
