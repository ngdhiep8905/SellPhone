package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.*;
import com.ptmhdv.SellPhone.Repository.OrdersRepository;
import com.ptmhdv.SellPhone.Repository.PaymentRepository;
import com.ptmhdv.SellPhone.Repository.PhonesRepository;
import com.ptmhdv.SellPhone.Repository.UsersRepository;
import com.ptmhdv.SellPhone.dto.OrdersDTO;
import com.ptmhdv.SellPhone.dto.OrdersPhonesDTO;
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
    private final PhonesRepository phonesRepo; // Nếu cần trừ kho
    private final CouponService couponService; // Nếu xử lý coupon
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

        Orders order = new Orders();

        // Gán thông tin người nhận
        order.setRecipientName(receiverName);
        order.setRecipientPhone(receiverPhone);
        order.setShippingAddress(receiverAddress);

        // Set User
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);

        // Set Payment
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        order.setPayment(payment);

        // Lấy giỏ hàng của user
        List<CartItem> cartItems = user.getCartItems();

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể checkout.");
        }

        // Tạo danh sách order items
        List<OrdersPhones> orderPhones = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cart : cartItems) {

            OrdersPhones item = new OrdersPhones();

            item.setOrder(order);
            item.setPhone(cart.getPhone());
            item.setQuantity(cart.getQuantity());
            item.setPrice(cart.getPhone().getPrice());

            BigDecimal itemTotal =
                    cart.getPhone().getPrice()
                            .multiply(BigDecimal.valueOf(cart.getQuantity()));

            total = total.add(itemTotal);

            orderPhones.add(item);

            // Nếu muốn trừ kho:
            // Phones phone = cart.getPhone();
            // phone.setStock(phone.getStock() - cart.getQuantity());
            // phonesRepo.save(phone);
        }

        // Xử lý coupon nếu có
        if (couponCode != null && !couponCode.isEmpty()) {
            BigDecimal discount = couponService.applyCoupon(couponCode, total);
            total = total.subtract(discount);
        }

        order.setTotalPrice(total);
        order.setOrderPhones(orderPhones);
        order.setStatus("PENDING");

        // Lưu đơn hàng + order details
        Orders saved = ordersRepo.save(order);

        // Xóa giỏ hàng sau khi checkout
        user.getCartItems().clear();

        return saved;
    }
}


