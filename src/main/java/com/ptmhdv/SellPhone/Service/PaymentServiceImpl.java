package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Payment;
import com.ptmhdv.SellPhone.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment update(Integer id, Payment updated) {
        Payment exist = getById(id);
        exist.setPaymentMethod(updated.getPaymentMethod());
        return paymentRepository.save(exist);
    }

    @Override
    public void delete(Integer id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment getById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}
