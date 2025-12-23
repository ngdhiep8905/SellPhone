package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private String fullName;
    private String phone;
    private String address;
    private String paymentMethodId;
    private String couponCode;


    private List<String> cartItemIds;
}
