package com.ptmhdv.SellPhone.order.service;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository;
import com.ptmhdv.SellPhone.cart.repository.CartRepository;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.order.dto.CheckoutRequest;
import com.ptmhdv.SellPhone.order.dto.CheckoutResponse;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.mapper.OrdersMapper;
import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
import com.ptmhdv.SellPhone.payment.service.PayosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.exception.PayOSException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String PM_BANKING = "02"; // paymentMethodId cho chuyển khoản
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_AWAITING_PAYMENT = "AWAITING_PAYMENT";

    private final OrdersRepository ordersRepo;
    private final PhonesRepository phonesRepo;
    private final PaymentRepository paymentRepo;

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;

    private final PayosService payosService;

    @Transactional
    public CheckoutResponse checkoutGuest(String cartToken, CheckoutRequest req) {

        if (cartToken == null || cartToken.isBlank()) {
            throw new RuntimeException("Thiếu CART_TOKEN (giỏ hàng chưa được khởi tạo)");
        }

        Cart cart = cartRepo.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        // Validate người nhận
        if (req.getFullName() == null || req.getFullName().isBlank())
            throw new RuntimeException("Vui lòng nhập họ tên người nhận");
        if (req.getPhone() == null || req.getPhone().isBlank())
            throw new RuntimeException("Vui lòng nhập số điện thoại");
        if (req.getAddress() == null || req.getAddress().isBlank())
            throw new RuntimeException("Vui lòng nhập địa chỉ giao hàng");
        if (req.getPaymentMethodId() == null || req.getPaymentMethodId().isBlank())
            throw new RuntimeException("Vui lòng chọn phương thức thanh toán");

        // Validate danh sách item được chọn
        if (req.getCartItemIds() == null || req.getCartItemIds().isEmpty()) {
            throw new RuntimeException("Bạn chưa chọn sản phẩm để thanh toán");
        }

        // Lấy đúng item thuộc cart hiện tại bằng query DB
        List<CartItem> selectedItems =
                cartItemRepo.findByCartIdAndCartItemIdIn(cart.getCartId(), req.getCartItemIds());

        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm đã chọn trong giỏ hàng");
        }

        // Client không được gửi ID rác
        if (selectedItems.size() != req.getCartItemIds().size()) {
            throw new RuntimeException("Có sản phẩm không hợp lệ trong danh sách thanh toán");
        }

        Orders order = new Orders();
        order.setUser(cart.getUser()); // có thể null nếu guest
        order.setRecipientName(req.getFullName());
        order.setRecipientPhone(req.getPhone());
        order.setShippingAddress(req.getAddress());

        Payment payment = paymentRepo.findById(req.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));
        order.setPayment(payment);

        List<OrdersPhones> orderDetails = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (CartItem ci : selectedItems) {
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

            OrdersPhones detail = new OrdersPhones();
            detail.setOrder(order);
            detail.setPhone(phone);
            detail.setQuantity(orderQty);
            detail.setPrice(phone.getPrice());
            detail.setTotalPrice(lineTotal);

            orderDetails.add(detail);
        }

        order.setTotalAmount(totalOrderAmount);
        order.setOrderPhones(orderDetails);

        // Nếu BANKING thì chờ thanh toán, COD thì pending như cũ
        if (PM_BANKING.equals(req.getPaymentMethodId())) {
            order.setStatus(STATUS_AWAITING_PAYMENT);
        } else {
            order.setStatus(STATUS_PENDING);
        }

        Orders saved = ordersRepo.save(order);

        // chỉ xóa các item đã mua
        cartItemRepo.deleteAll(selectedItems);

        CheckoutResponse resp = new CheckoutResponse();
        resp.setOrder(OrdersMapper.toDTO(saved));

        // Option A: tạo PayOS checkoutUrl nếu BANKING
        if (PM_BANKING.equals(req.getPaymentMethodId())) {
            try {
                // PayOS doc ví dụ orderCode dùng timestamp/1000 :contentReference[oaicite:4]{index=4}
                long payosOrderCode = System.currentTimeMillis() / 1000;

                long amountVnd = saved.getTotalAmount().longValue(); // VND, không nên có phần lẻ
                String description = "Don " + saved.getOrderId();  // ví dụ: "Don O202512221234"

                var paymentLink = payosService.createPaymentLink(payosOrderCode, amountVnd, description, saved.getOrderId());

                saved.setPayosOrderCode(payosOrderCode);
                saved.setPayosPaymentLinkId(paymentLink.getPaymentLinkId());
                ordersRepo.save(saved);

                resp.setCheckoutUrl(paymentLink.getCheckoutUrl());
            } catch (PayOSException e) {
                throw new RuntimeException("Không tạo được link thanh toán PayOS: " + e.getMessage());
            }
        } else {
            resp.setCheckoutUrl(null);
        }

        return resp;
    }
}
