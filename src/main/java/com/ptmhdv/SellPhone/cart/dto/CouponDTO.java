package com.ptmhdv.SellPhone.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponDTO {
    private String id;
    private String code;
    private BigDecimal value;
    private String status;
}

