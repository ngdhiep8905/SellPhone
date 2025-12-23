package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersDTO {
    private String orderId;          // ✅ đổi id -> orderId
    private String userId;
    private String status;
    private String paymentId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private BigDecimal totalAmount;  // ✅ đổi totalPrice -> totalAmount
    private List<OrdersPhonesDTO> orderItems;
}
