package com.ptmhdv.SellPhone.cart.dto;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemViewDTO {
    private String cartItemId;
    private Integer quantityPrice;
    private PhonesDTO phone;

    public void setQuantity(@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
    }
}
