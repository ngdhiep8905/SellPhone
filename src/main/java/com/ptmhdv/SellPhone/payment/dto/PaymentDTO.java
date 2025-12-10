package com.ptmhdv.SellPhone.payment.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentDTO {
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;

    @Size(max = 50)
    @Column(name = "payment_status", length = 50)
    private String paymentStatus;
}
