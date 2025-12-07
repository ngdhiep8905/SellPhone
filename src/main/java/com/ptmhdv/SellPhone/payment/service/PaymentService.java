package com.ptmhdv.sellphone.payment.service;

import com.ptmhdv.sellphone.payment.entity.Payment;
import com.ptmhdv.sellphone.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    public List<Payment> getAll() {
        return paymentRepo.findAll();
    }

    public Payment getById(String id) {
        return paymentRepo.findById(id).orElse(null);
    }
}
