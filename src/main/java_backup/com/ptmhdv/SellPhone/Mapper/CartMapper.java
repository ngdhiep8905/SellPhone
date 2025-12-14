package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.Cart;
import com.ptmhdv.SellPhone.Entity.CartItem;
import com.ptmhdv.SellPhone.dto.CartDTO;
import com.ptmhdv.SellPhone.dto.CartItemDTO;

import java.util.stream.Collectors;

public class CartMapper {
    public static CartItemDTO toItemDTO(CartItem e) {
        CartItemDTO d = new CartItemDTO();
        d.setId(e.getCartItemId());
        d.setCartId(e.getCart().getCartId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantityPrice());
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
