package com.ptmhdv.SellPhone.payment.service;

import com.ptmhdv.sellphone.common.exception.ResourceNotFoundException;
import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.dto.PaymentDTO;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final OrdersRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrdersRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment processOrderPayment(String orderId, PaymentDTO dto) {
        log.info("Processing payment for order: {}", orderId);

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(orderId));

        Payment payment = new Payment();
        List<Orders> orderList = new ArrayList<>();
        orderList.add(order);
        payment.setOrders(orderList);
        payment.setOrders(orderList);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(dto.getPaymentStatus());

        try {
            payment.setPaymentStatus("COMPLETED");
            payment.setPaidAt(LocalDateTime.now());

            // Update order status
            order.setStatus("PAID");
            orderRepository.save(order);

            log.info("Payment processed successfully");
        } catch (Exception e) {
            payment.setPaymentStatus("FAILED");
            log.error("Payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrder(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(orderId));
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException( orderId));
    }

    @Override
    public Payment updatePaymentStatus(String paymentId, String status) {
        log.info("Updating payment {} status to {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(paymentId));

        payment.setPaymentStatus(status);
        if ("COMPLETED".equals(status) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }
}
