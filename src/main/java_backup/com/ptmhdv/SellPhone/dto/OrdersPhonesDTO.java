package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersPhonesDTO {
    private String id;
    private String orderId;
    private String phoneId;
    private Integer quantity;
    private BigDecimal price;


}
