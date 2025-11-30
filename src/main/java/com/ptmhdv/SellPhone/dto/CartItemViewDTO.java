package com.ptmhdv.SellPhone.dto;

import lombok.Data;

@Data
public class CartItemViewDTO {
    private Long cartItemId;
    private Integer quantityPrice; // dùng đúng tên FE check
    private PhonesDTO phone;       // embed full phone object để FE render
}
