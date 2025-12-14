package com.ptmhdv.SellPhone.payment.service;

import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    public List<Payment> getAll() {
        return paymentRepo.findAll();
    }

    // Không trả null nữa -> 404 rõ ràng
    public Payment getByIdOrThrow(String id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND"
                ));
    }

    public Payment create(Payment payment) {
        // Nếu client gửi paymentId và bị trùng -> 409
        if (payment.getPaymentId() != null && paymentRepo.existsById(payment.getPaymentId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "PAYMENT_ID_EXISTS");
        }

        // Chuẩn hoá dữ liệu cơ bản
        if (payment.getPaymentMethod() != null) {
            payment.setPaymentMethod(payment.getPaymentMethod().trim());
        }
        if (payment.getPaymentStatus() != null) {
            payment.setPaymentStatus(payment.getPaymentStatus().trim());
        }

        // paymentId sẽ tự sinh ở @PrePersist nếu null
        return paymentRepo.save(payment);
    }

    public Payment update(String id, Payment req) {
        Payment existing = getByIdOrThrow(id);

        // Update paymentMethod nếu có gửi lên
        if (req.getPaymentMethod() != null) {
            String method = req.getPaymentMethod().trim();
            if (method.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAYMENT_METHOD_REQUIRED");
            }
            if (method.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAYMENT_METHOD_TOO_LONG");
            }
            existing.setPaymentMethod(method);
        }

        // Update paymentStatus nếu có gửi lên
        if (req.getPaymentStatus() != null) {
            String status = req.getPaymentStatus().trim();
            if (status.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAYMENT_STATUS_TOO_LONG");
            }
            existing.setPaymentStatus(status);
        }

        return paymentRepo.save(existing);
    }

    public void delete(String id) {
        if (!paymentRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND");
        }
        paymentRepo.deleteById(id);
    }
}
