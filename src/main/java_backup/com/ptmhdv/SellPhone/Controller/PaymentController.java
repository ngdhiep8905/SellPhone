package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Entity.Payment;
import com.ptmhdv.SellPhone.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable String id) {
        return paymentService.getById(id);
    }
}

