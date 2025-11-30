package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartViewDTO {
    private Long cartId;
    private List<CartItemViewDTO> items;
}
