package com.ptmhdv.SellPhone.order.dto;

import lombok.Data;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderAdminDetailDTO {
    private String orderId;

    private String customerName;
    private String customerPhone;

    private LocalDateTime bookDate;
    private BigDecimal totalPrice;

    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;

    private String status;

    private String paymentMethod;
    private String paymentStatus;

    private String rejectReason;

    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {
        private String phoneId;
        private String phoneName;
        private Integer quantity;
        private BigDecimal price;
    }
}
