package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartViewDTO {
    private String cartId;
    private List<CartItemViewDTO> items;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
