package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersDTO {
    private String id;
    private String userId;
    private String status;
    private String paymentId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private BigDecimal totalPrice; // Quan trọng để FE tính tiền QR
    private List<OrdersPhonesDTO> orderItems;
}