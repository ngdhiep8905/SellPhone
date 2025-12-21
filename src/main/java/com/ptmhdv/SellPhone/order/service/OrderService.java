package com.ptmhdv.SellPhone.order.service;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository;
import com.ptmhdv.SellPhone.cart.repository.CartRepository;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.order.dto.CheckoutRequest;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
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

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;

    @Transactional
    public Orders checkoutGuest(String cartToken, CheckoutRequest req) {

        if (cartToken == null || cartToken.isBlank()) {
            throw new RuntimeException("Thiếu CART_TOKEN (giỏ hàng chưa được khởi tạo)");
        }

        Cart cart = cartRepo.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        // Validate thông tin người nhận
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new RuntimeException("Vui lòng nhập họ tên người nhận");
        }
        if (req.getPhone() == null || req.getPhone().isBlank()) {
            throw new RuntimeException("Vui lòng nhập số điện thoại");
        }
        if (req.getAddress() == null || req.getAddress().isBlank()) {
            throw new RuntimeException("Vui lòng nhập địa chỉ giao hàng");
        }
        if (req.getPaymentMethodId() == null || req.getPaymentMethodId().isBlank()) {
            throw new RuntimeException("Vui lòng chọn phương thức thanh toán");
        }

        Orders order = new Orders();

        // Nếu khách đã login và cart đã attach -> user sẽ khác null
        order.setUser(cart.getUser());

        order.setRecipientName(req.getFullName());
        order.setRecipientPhone(req.getPhone());
        order.setShippingAddress(req.getAddress());

        Payment payment = paymentRepo.findById(req.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));
        order.setPayment(payment);

        List<OrdersPhones> orderDetails = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (CartItem ci : cartItems) {
            Phones phone = ci.getPhone();
            int orderQty = ci.getQuantity();

            if (phone.getStockQuantity() < orderQty) {
                throw new RuntimeException("Sản phẩm " + phone.getPhoneName() + " không đủ hàng trong kho!");
            }

            // Trừ kho
            phone.setStockQuantity(phone.getStockQuantity() - orderQty);
            phonesRepo.save(phone);

            // Tính tiền
            BigDecimal lineTotal = phone.getPrice().multiply(BigDecimal.valueOf(orderQty));
            totalOrderAmount = totalOrderAmount.add(lineTotal);

            // Detail (KHÔNG set id thủ công; @MapsId tự map)
            OrdersPhones detail = new OrdersPhones();
            detail.setOrder(order);
            detail.setPhone(phone);
            detail.setQuantity(orderQty);
            detail.setPrice(phone.getPrice());
            detail.setTotalPrice(lineTotal);

            orderDetails.add(detail);
        }

        order.setTotalPrice(totalOrderAmount);
        order.setOrderPhones(orderDetails);
        order.setStatus("PENDING");

        // Lưu order (Cascade ALL sẽ lưu luôn order_details)
        Orders saved = ordersRepo.save(order);

        // Xóa cart items (clear giỏ)
        cartItemRepo.deleteByCart_CartId(cart.getCartId());

        return saved;
    }
}
