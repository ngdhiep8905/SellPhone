package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

@Data
public class CheckoutResponse {
    private OrdersDTO order;
    private String checkoutUrl; // PayOS hosted checkout page (Option A)
}
