package com.ptmhdv.SellPhone.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Integer id;   // <-- đổi String -> Integer
    private String cartId;
    private String phoneId;
    private Integer quantity;
    private String phoneName;
    private String image;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
