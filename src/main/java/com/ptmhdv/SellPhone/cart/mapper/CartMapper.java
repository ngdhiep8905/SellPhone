package com.ptmhdv.SellPhone.cart.mapper;

import com.ptmhdv.SellPhone.cart.dto.CartItemDTO;
import com.ptmhdv.SellPhone.cart.entity.CartItem;

import java.math.BigDecimal;

public class CartMapper {

    public static CartItemDTO toItemDTO(CartItem e) {
        CartItemDTO d = new CartItemDTO();

        d.setId(e.getCartItemId()); // Integer -> Integer OK
        d.setCartId(e.getCart().getCartId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());

        // extra fields
        d.setPhoneName(e.getPhone().getPhoneName());
        d.setImage(e.getPhone().getCoverImageURL());
        d.setPrice(e.getPhone().getPrice());

        BigDecimal price = e.getPhone().getPrice() != null ? e.getPhone().getPrice() : BigDecimal.ZERO;
        d.setTotalPrice(price.multiply(BigDecimal.valueOf(e.getQuantity())));

        return d;
    }
}
