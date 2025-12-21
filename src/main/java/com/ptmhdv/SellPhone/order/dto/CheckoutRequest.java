package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String fullName;
    private String phone;
    private String address;
    private String paymentMethodId;
    private String couponCode;
}
