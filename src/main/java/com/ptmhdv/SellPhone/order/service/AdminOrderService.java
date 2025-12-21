package com.ptmhdv.SellPhone.order.service;

import com.ptmhdv.SellPhone.order.dto.OrderAdminDetailDTO;
import com.ptmhdv.SellPhone.order.dto.OrderAdminRowDTO;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrdersRepository ordersRepo;

    public List<OrderAdminRowDTO> list(String status, String paymentMethod, String paymentStatus) {
        List<Orders> orders = ordersRepo.adminFilter(nullIfBlank(status), nullIfBlank(paymentMethod), nullIfBlank(paymentStatus));
        return orders.stream().map(this::toRow).toList();
    }

    public OrderAdminDetailDTO detail(String id) {
        Orders o = ordersRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn: " + id));
        return toDetail(o);
    }

    @Transactional
    public void confirm(String id) {
        Orders o = ordersRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn: " + id));

        if (!"PENDING".equals(o.getStatus())) {
            throw new RuntimeException("Chỉ đơn 'Cần xác nhận' mới được xác nhận.");
        }

        String method = o.getPayment() != null ? o.getPayment().getPaymentMethod() : null;
        String payStatus = o.getPayment() != null ? o.getPayment().getPaymentStatus() : null;

        // Rule của bạn:
        // - COD: xác nhận được
        // - BANKING: chỉ xác nhận nếu PAID
        if ("BANKING".equals(method) && !"PAID".equals(payStatus)) {
            throw new RuntimeException("Đơn chuyển khoản chưa thanh toán, không thể xác nhận.");
        }

        // PENDING -> SHIPPED (đã xác nhận, đang vận chuyển)
        o.setStatus("SHIPPED");
        ordersRepo.save(o);
    }

    @Transactional
    public void delivered(String id) {
        Orders o = ordersRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn: " + id));

        if (!"SHIPPED".equals(o.getStatus())) {
            throw new RuntimeException("Chỉ đơn 'Đang vận chuyển' mới được xác nhận giao thành công.");
        }

        o.setStatus("DELIVERED");
        ordersRepo.save(o);
    }

    @Transactional
    public void reject(String id, String reason) {
        Orders o = ordersRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn: " + id));

        if (!("PENDING".equals(o.getStatus()) || "SHIPPED".equals(o.getStatus()))) {
            throw new RuntimeException("Chỉ được hủy khi đơn đang 'Cần xác nhận' hoặc 'Đang vận chuyển'.");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do hủy.");
        }

        o.setStatus("CANCELLED");
        o.setRejectReason(reason.trim());
        ordersRepo.save(o);
    }


    // ===== mapping =====
    private OrderAdminRowDTO toRow(Orders o) {
        OrderAdminRowDTO dto = new OrderAdminRowDTO();
        dto.setOrderId(o.getOrderId());
        dto.setBookDate(o.getBookDate());
        dto.setTotalPrice(o.getTotalPrice());
        dto.setShippingAddress(o.getShippingAddress());
        dto.setStatus(o.getStatus());

        dto.setCustomerName(o.getUser() != null ? safe(o.getUser().getFullName(), "Khách hàng") : "Khách hàng");
        dto.setPaymentMethod(o.getPayment() != null ? o.getPayment().getPaymentMethod() : null);
        dto.setPaymentStatus(o.getPayment() != null ? o.getPayment().getPaymentStatus() : null);

        String preview = "";
        if (o.getOrderPhones() != null) {
            preview = o.getOrderPhones().stream()
                    .map(op -> safe(op.getPhone().getPhoneName(), op.getPhone().getPhoneId()) + " x" + op.getQuantity())
                    .limit(4)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
        }
        dto.setItemsPreview(preview);
        return dto;
    }

    private OrderAdminDetailDTO toDetail(Orders o) {
        OrderAdminDetailDTO dto = new OrderAdminDetailDTO();
        dto.setOrderId(o.getOrderId());
        dto.setBookDate(o.getBookDate());
        dto.setTotalPrice(o.getTotalPrice());
        dto.setRecipientName(o.getRecipientName());
        dto.setRecipientPhone(o.getRecipientPhone());
        dto.setShippingAddress(o.getShippingAddress());
        dto.setStatus(o.getStatus());
        dto.setRejectReason(o.getRejectReason());

        dto.setCustomerName(o.getUser() != null ? safe(o.getUser().getFullName(), "Khách hàng") : "Khách hàng");
        dto.setCustomerPhone(o.getUser() != null ? o.getUser().getPhone() : null);

        dto.setPaymentMethod(o.getPayment() != null ? o.getPayment().getPaymentMethod() : null);
        dto.setPaymentStatus(o.getPayment() != null ? o.getPayment().getPaymentStatus() : null);

        List<OrderAdminDetailDTO.ItemDTO> items = (o.getOrderPhones() == null) ? List.of()
                : o.getOrderPhones().stream().map(this::toItem).toList();
        dto.setItems(items);

        return dto;
    }

    private OrderAdminDetailDTO.ItemDTO toItem(OrdersPhones op) {
        OrderAdminDetailDTO.ItemDTO it = new OrderAdminDetailDTO.ItemDTO();
        it.setPhoneId(op.getPhone().getPhoneId());
        it.setPhoneName(op.getPhone().getPhoneName());
        it.setQuantity(op.getQuantity());
        it.setPrice(op.getPrice());
        return it;
    }

    private String nullIfBlank(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }
}
