package com.ptmhdv.SellPhone.payment.controller;

import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.service.PaymentService;
import jakarta.validation.Valid;
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
        return paymentService.getByIdOrThrow(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Payment create(@Valid @RequestBody Payment payment) {
        return paymentService.create(payment);
    }

    // KHÔNG @Valid ở đây để cho phép update từng phần (partial)
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public Payment update(@PathVariable String id, @RequestBody Payment req) {
        return paymentService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        paymentService.delete(id);
    }
}
