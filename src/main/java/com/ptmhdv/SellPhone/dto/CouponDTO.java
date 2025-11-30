package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponDTO {
    private Long id;
    private String code;
    private BigDecimal value;
    private String status;
}

