package com.ptmhdv.SellPhone.payment.repository;

import com.ptmhdv.SellPhone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
