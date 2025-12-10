package com.ptmhdv.SellPhone.payment.repository;

import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrder(Orders order);
}
