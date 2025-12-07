package com.ptmhdv.SellPhone.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersDTO {
    private String id;
    private String userId;
    private LocalDateTime createdAt;
    private String status;
    private List<OrdersPhonesDTO> orderItems;
    private String paymentId;

    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;

}
