package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentMethod(String paymentMethod);

    boolean existsByPaymentMethod(String paymentMethod);
}