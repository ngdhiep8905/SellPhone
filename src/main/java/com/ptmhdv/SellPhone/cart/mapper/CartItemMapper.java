package com.ptmhdv.sellphone.cart.mapper;

import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.cart.dto.CartItemDTO;

public class CartItemMapper {
    public static CartItemDTO toDTO(CartItem e) {
        CartItemDTO d = new CartItemDTO();
        d.setId(e.getCartItemId());
        d.setCartId(e.getCart().getCartId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());

        d.setPrice(e.getPhone().getPrice());  // giá 1 sản phẩm
        d.setTotalPrice(e.getQuantityPrice()); // tổng tiền

        d.setPhoneName(e.getPhone().getPhoneName());
        d.setImage(e.getPhone().getCoverImageURL());

        return d;
    }

}
