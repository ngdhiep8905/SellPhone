package com.ptmhdv.SellPhone.payment.service;

import com.ptmhdv.SellPhone.payment.dto.PaymentDTO;
import com.ptmhdv.SellPhone.payment.entity.Payment;

public interface PaymentService {
    Payment processOrderPayment(String orderId, PaymentDTO dto);

    Payment getPaymentByOrder(String orderId);

    Payment updatePaymentStatus(String paymentId, String status);
}
