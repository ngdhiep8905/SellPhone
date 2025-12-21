package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

import java.math.BigDecimal;

import java.time.LocalDateTime;

@Data
public class OrderAdminRowDTO {
    private String orderId;
    private String customerName;
    private LocalDateTime bookDate;     // hiện entity là LocalDate
    private BigDecimal totalPrice;

    private String shippingAddress;
    private String status;

    private String paymentMethod;   // COD/BANKING
    private String paymentStatus;   // PENDING/PAID

    private String itemsPreview;    // "iPhone 15 x1; S24 x2"
}
