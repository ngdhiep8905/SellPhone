package com.ptmhdv.SellPhone.payment.mapper;

import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.dto.PaymentDTO;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment e) {
        PaymentDTO d = new PaymentDTO();
        d.setId(e.getPaymentId());
        d.setPaymentMethod(e.getPaymentMethod());
        d.setStatus(e.getPaymentStatus());
        return d;
    }
}
