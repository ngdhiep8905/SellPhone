package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.Payment;
import com.ptmhdv.SellPhone.dto.PaymentDTO;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment e) {
        PaymentDTO d = new PaymentDTO();
        d.setId(e.getPaymentId());
        d.setPaymentMethod(e.getPaymentMethod());
        d.setStatus(e.getPaymentMethod());
        return d;
    }
}
