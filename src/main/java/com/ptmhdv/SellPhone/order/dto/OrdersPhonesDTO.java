package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersPhonesDTO {
    private String id;
    private String orderId;
    private String phoneId;
    private int quantity;
    private BigDecimal price;


}
