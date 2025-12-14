package com.ptmhdv.sellphone.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private String id;
    private String cartId;
    private String phoneId;
    private Integer quantity;
    private String phoneName;
    private String image;
    private BigDecimal price;      // giá 1 sản phẩm
    private BigDecimal totalPrice; // giá * số lượng  <-- field mới
}

