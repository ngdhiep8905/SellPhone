package com.ptmhdv.SellPhone.dto;

import lombok.Data;

@Data
public class CartItemViewDTO {
    private String cartItemId;
    private Integer quantityPrice;
    private PhonesDTO phone;
}
