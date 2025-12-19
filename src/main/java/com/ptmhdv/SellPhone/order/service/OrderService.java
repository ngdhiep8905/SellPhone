package com.ptmhdv.SellPhone.order.service;

import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository;
import com.ptmhdv.SellPhone.order.dto.CheckoutRequest;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.entity.OrdersPhonesId;
import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepo;
    private final PhonesRepository phonesRepo;
    private final PaymentRepository paymentRepo;
    private final UsersRepository usersRepo;
    private final CartItemRepository cartItemRepo;

    @Transactional
    public Orders checkout(CheckoutRequest req) {

        // 1. Lấy thông tin User
        Users user = usersRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<CartItem> cartItems = user.getCartItems();
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // 2. Khởi tạo Đơn hàng
        Orders order = new Orders();
        // ID tự sinh ở @PrePersist hoặc gán thủ công nếu muốn khớp logic FE
        order.setUser(user);
        order.setRecipientName(req.getRecipientName());
        order.setRecipientPhone(req.getRecipientPhone());
        order.setShippingAddress(req.getShippingAddress());

        Payment payment = paymentRepo.findById(req.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));
        order.setPayment(payment);

        List<OrdersPhones> orderDetails = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        // 3. Xử lý từng sản phẩm: Kiểm tra kho + Trừ kho + Tạo detail
        for (CartItem cart : cartItems) {
            Phones phone = cart.getPhone();
            int orderQty = cart.getQuantity();

            // --- QUAN TRỌNG: KIỂM TRA TỒN KHO ---
            if (phone.getStockQuantity() < orderQty) {
                throw new RuntimeException("Sản phẩm " + phone.getPhoneName() + " không đủ hàng trong kho!");
            }

            // --- TRỪ KHO ---
            phone.setStockQuantity(phone.getStockQuantity() - orderQty);
            phonesRepo.save(phone);

            // Tính tiền
            BigDecimal lineTotal = phone.getPrice().multiply(BigDecimal.valueOf(orderQty));
            totalOrderAmount = totalOrderAmount.add(lineTotal);

            // Tạo Detail
            OrdersPhones detail = new OrdersPhones();
            detail.setOrder(order);
            detail.setPhone(phone);
            detail.setQuantity(orderQty);
            detail.setPrice(phone.getPrice());
            detail.setTotalPrice(lineTotal);
            // Gán Id phức hợp (nếu dùng EmbeddedId)
            detail.setId(new OrdersPhonesId(order.getOrderId(), phone.getPhoneId()));

            orderDetails.add(detail);
        }

        // 4. Lưu đơn hàng (Cascade ALL sẽ lưu luôn details)
        order.setTotalPrice(totalOrderAmount);
        order.setOrderPhones(orderDetails);
        order.setStatus("PENDING");

        Orders savedOrder = ordersRepo.save(order);

        // 5. Xóa giỏ hàng
        cartItemRepo.deleteAll(cartItems);
        user.getCartItems().clear();

        return savedOrder;
    }
}