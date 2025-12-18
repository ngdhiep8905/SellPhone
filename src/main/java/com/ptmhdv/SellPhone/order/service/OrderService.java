package com.ptmhdv.SellPhone.order.service;

import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository; // [BỔ SUNG REPO]
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.entity.OrdersPhonesId;
import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.repository.PaymentRepository;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.repository.UsersRepository;
import jakarta.transaction.Transactional; // Dùng @Transactional cho toàn bộ quá trình
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    // [BỔ SUNG]: Cần có Repository để xóa CartItem một cách rõ ràng
    private final CartItemRepository cartItemRepo;

    @Transactional // Đảm bảo tất cả các thao tác (lưu order, xóa cart) thành công hoặc thất bại cùng nhau
    public Orders checkout(
            String userId,
            String receiverName,
            String receiverAddress,
            String receiverPhone,
            String couponCode,
            String paymentId
    ) {

        // ====== 1) LẤY USER VÀ CART ITEMS ======
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy danh sách cart items cần xóa sau này
        List<CartItem> cartItemsToDelete = user.getCartItems();

        if (cartItemsToDelete.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống! Không thể thanh toán.");
        }

        // ====== 2) KHỞI TẠO ORDER (Header) ======
        Orders order = new Orders();
        long v = System.currentTimeMillis() % 1_000_000_000_000L; // 12 digits
        order.setOrderId("O" + String.format("%012d", v));        // O + 12 = 13 chars

        order.setUser(user);
        order.setRecipientName(receiverName);
        order.setRecipientPhone(receiverPhone);
        order.setShippingAddress(receiverAddress);

        // [BỔ SUNG] Xử lý Coupon Code (Giả định Orders Entity có trường setCouponCode)


        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        order.setPayment(payment);

        List<OrdersPhones> orderPhonesList = new ArrayList<>();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;



        for (CartItem cart : cartItemsToDelete) {

            // [LƯU Ý]: Đảm bảo cart.getPhone().getPrice() không null
            BigDecimal price = cart.getPhone().getPrice();
            int qty = cart.getQuantity();

            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            totalOrderAmount = totalOrderAmount.add(lineTotal);

            // Tạo record order_details
            OrdersPhones detail = new OrdersPhones();
            detail.setId(new OrdersPhonesId(order.getOrderId(), cart.getPhone().getPhoneId()));
            detail.setOrder(order);
            detail.setPhone(cart.getPhone());
            detail.setQuantity(qty);
            detail.setPrice(price);
            detail.setTotalPrice(lineTotal);

            orderPhonesList.add(detail);
        }

        // [BỔ SUNG] 4) ÁP DỤNG GIẢM GIÁ (Nếu có logic phức tạp)
        // totalOrderAmount = totalOrderAmount.subtract(discountAmount);


        // ====== 5) LƯU ORDER ======
        order.setTotalPrice(totalOrderAmount);
        order.setOrderPhones(orderPhonesList); // Yêu cầu Orders Entity phải có CascadeType.ALL
        order.setStatus("PENDING");

        Orders savedOrder = ordersRepo.save(order);

        // ====== 6) XÓA GIỎ HÀNG (QUAN TRỌNG) ======
        // [SỬA LỖI] Xóa rõ ràng từng CartItem hoặc sử dụng deleteByEntity
        cartItemRepo.deleteAll(cartItemsToDelete);

        // Sau khi xóa khỏi DB, cần xóa khỏi bộ nhớ user nếu bạn định dùng lại đối tượng user này
        user.getCartItems().clear();

        return savedOrder;
    }
}