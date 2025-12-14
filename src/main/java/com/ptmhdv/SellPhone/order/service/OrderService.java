package com.ptmhdv.sellphone.order.service;


import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.cart.service.CouponService;
import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.entity.OrdersPhones;
import com.ptmhdv.sellphone.order.repository.OrdersRepository;
import com.ptmhdv.sellphone.payment.entity.Payment;
import com.ptmhdv.sellphone.payment.repository.PaymentRepository;
import com.ptmhdv.sellphone.catalog.repository.PhonesRepository;
import com.ptmhdv.sellphone.user.entity.Users;
import com.ptmhdv.sellphone.user.repository.UsersRepository;
import com.ptmhdv.sellphone.order.dto.OrdersDTO;
import com.ptmhdv.sellphone.order.dto.OrdersPhonesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepo;
    private final PhonesRepository phonesRepo;
    private final CouponService couponService;
    private final PaymentRepository paymentRepo;
    private final UsersRepository usersRepo;

    public Orders checkout(
            String userId,
            String receiverName,
            String receiverAddress,
            String receiverPhone,
            String couponCode,
            String paymentId
    ) {

        // ====== 1) LẤY USER ======
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = user.getCartItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống! Không thể thanh toán.");
        }

        // ====== 2) KHỞI TẠO ORDER ======
        Orders order = new Orders();
        order.setUser(user);
        order.setRecipientName(receiverName);
        order.setRecipientPhone(receiverPhone);
        order.setShippingAddress(receiverAddress);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        order.setPayment(payment);

        List<OrdersPhones> orderPhonesList = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        // ====== 3) TÍNH TIỀN & TẠO ORDER DETAILS ======
        for (CartItem cart : cartItems) {

            BigDecimal price = cart.getPhone().getPrice(); // giá 1 sp
            int qty = cart.getQuantity();

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            // Cộng tổng đơn
            totalOrderAmount = totalOrderAmount.add(lineTotal);

            // Tạo record order_details
            OrdersPhones detail = new OrdersPhones();
            detail.setOrder(order);
            detail.setPhone(cart.getPhone());
            detail.setQuantity(qty);
            detail.setPrice(price);
            detail.setTotalPrice(lineTotal);  // map vào cột total_price của SQL

            orderPhonesList.add(detail);
        }

        // ====== 4) XỬ LÝ COUPON ======
        if (couponCode != null && !couponCode.isBlank()) {
            BigDecimal discount = couponService.applyCoupon(couponCode, totalOrderAmount);
            totalOrderAmount = totalOrderAmount.subtract(discount);
        }

        // ====== 5) LƯU ORDER ======
        order.setTotalPrice(totalOrderAmount);
        order.setOrderPhones(orderPhonesList);
        order.setStatus("PENDING");

        Orders savedOrder = ordersRepo.save(order);

        // ====== 6) XÓA GIỎ HÀNG ======
        cartItems.clear();

        return savedOrder;
    }
}
