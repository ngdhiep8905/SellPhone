package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment create(Payment payment);

    Payment update(Integer id, Payment payment);

    void delete(Integer id);

    Payment getById(Integer id);

    List<Payment> getAll();
}
