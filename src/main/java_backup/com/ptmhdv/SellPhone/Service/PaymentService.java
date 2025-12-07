package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Payment;
import com.ptmhdv.SellPhone.Repository.PaymentRepository;
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
