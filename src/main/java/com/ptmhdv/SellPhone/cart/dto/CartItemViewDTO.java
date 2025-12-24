package com.ptmhdv.SellPhone.cart.dto;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class CartItemViewDTO {
    private Integer cartItemId;
    private Integer quantity;
    private PhonesDTO phone;
}


