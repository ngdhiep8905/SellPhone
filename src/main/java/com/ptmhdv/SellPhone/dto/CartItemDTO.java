package com.ptmhdv.SellPhone.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long cartId;
    private Long phoneId;
    private Integer quantity;
}
