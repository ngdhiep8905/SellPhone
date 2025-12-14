package com.ptmhdv.sellphone.payment.repository;

import com.ptmhdv.sellphone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
