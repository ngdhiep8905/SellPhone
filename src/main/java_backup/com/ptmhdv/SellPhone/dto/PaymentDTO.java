package com.ptmhdv.SellPhone.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String id;
    private String paymentMethod;
    private String status;
}
