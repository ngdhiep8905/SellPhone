package com.ptmhdv.SellPhone.cart.mapper;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.dto.CartDTO;
import com.ptmhdv.SellPhone.cart.dto.CartItemDTO;

import java.util.stream.Collectors;

public class CartMapper {
    public static CartItemDTO toItemDTO(CartItem e) {
        CartItemDTO d = new CartItemDTO();
        d.setId(e.getCartItemId());
        d.setCartId(e.getCart().getCartId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());
        return d;
    }

    public static CartDTO toDTO(Cart e) {
        CartDTO d = new CartDTO();
        d.setId(e.getCartId());
        d.setUserId(e.getUser().getUserId());
        d.setItems(
                e.getItems().stream()
                        .map(CartMapper::toItemDTO)
                        .collect(Collectors.toList())
        );
        return d;
    }
}
