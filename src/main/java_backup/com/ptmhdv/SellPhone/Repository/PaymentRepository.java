package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
