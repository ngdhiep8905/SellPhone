package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersPhonesDTO {
    private Long id;
    private Long orderId;
    private Long phoneId;
    private Integer quantity;
    private BigDecimal price;

}
