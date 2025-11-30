package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.CartItem;
import com.ptmhdv.SellPhone.dto.CartItemDTO;

public class CartItemMapper {
    public static CartItemDTO toDTO(CartItem e) {
        if (e == null) return null;

        CartItemDTO d = new CartItemDTO();
        d.setId(e.getCartItemId());                       // hoặc getCartItemId()
        d.setCartId(e.getCart().getCartId());         // nếu là getCartId thì sửa lại
        d.setPhoneId(e.getPhone().getPhoneId());  // theo code bạn đang dùng
        d.setQuantity(e.getQuantityPrice());
        return d;
    }
}
