package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersDTO {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private String status;
    private Long paymentId;
    private List<OrdersPhonesDTO> orderItems;
}
