package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String userId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentId;
    private String couponCode;
}