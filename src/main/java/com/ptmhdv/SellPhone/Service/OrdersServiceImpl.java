package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.*;
import com.ptmhdv.SellPhone.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private final UsersRepository usersRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersPhonesRepository ordersPhonesRepository;

    @Override
    public Orders checkout(Long userId, String receiverName, String receiverAddress,
                           String receiverPhone, String couponCode, Integer paymentId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart empty"));

        if (cart.getItems().isEmpty())
            throw new RuntimeException("Cart is empty");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Coupon coupon = null;
        if (couponCode != null && !couponCode.isBlank()) {
            coupon = couponRepository.findByCodeAndStatus(couponCode, "ACTIVE")
                    .orElseThrow(() -> new RuntimeException("Invalid or inactive coupon"));

        }

        Orders order = new Orders();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPayment(payment);
        order.setCoupon(coupon);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);

        Orders saved = ordersRepository.save(order);

        // Chuyển CartItem -> OrdersPhones
        var ops = cart.getItems().stream()
                .map(ci -> {
                    OrdersPhones op = new OrdersPhones();
                    op.setOrder(saved);
                    op.setPhone(ci.getPhone());
                    op.setQuantity(ci.getQuantityPrice());
                    op.setPrice(ci.getPhone().getPrice());
                    return ordersPhonesRepository.save(op);
                })
                .collect(Collectors.toList());

        saved.setOrderPhones(ops);

        // Xóa giỏ hàng sau checkout
        cart.getItems().clear();

        return saved;
    }
}
