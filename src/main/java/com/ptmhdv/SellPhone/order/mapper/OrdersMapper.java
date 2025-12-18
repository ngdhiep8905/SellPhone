package com.ptmhdv.SellPhone.order.mapper;

import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.dto.OrdersDTO;
import com.ptmhdv.SellPhone.order.dto.OrdersPhonesDTO;

import java.util.stream.Collectors;

public class OrdersMapper {

    // [PHƯƠNG THỨC GIỮ NGUYÊN] Ánh xạ OrdersPhones sang DTO (Đã sửa lỗi Khóa tổng hợp)
    public static OrdersPhonesDTO toOrdersPhonesDTO(OrdersPhones e) {
        OrdersPhonesDTO d = new OrdersPhonesDTO();

        if (e.getId() != null) {
            d.setOrderId(e.getId().getOrder());
            d.setPhoneId(e.getId().getPhone());
        } else {
            d.setOrderId(e.getOrder().getOrderId());
            d.setPhoneId(e.getPhone().getPhoneId());
        }

        d.setQuantity(e.getQuantity());
        d.setPrice(e.getPrice());
        // d.setTotalPrice(e.getTotalPrice()); // Giữ lại nếu DTO có trường này

        return d;
    }

    // [PHƯƠNG THỨC ĐÃ SỬA] Bổ sung ánh xạ thông tin người nhận
    public static OrdersDTO toDTO(Orders e) {
        OrdersDTO d = new OrdersDTO();
        d.setId(e.getOrderId());

        // Giả định Entity Orders có phương thức getUser() để lấy thông tin user
        if (e.getUser() != null) {
            d.setUserId(e.getUser().getUserId());
        }

        d.setStatus(e.getStatus());
        if (e.getPayment() != null)
            d.setPaymentId(e.getPayment().getPaymentId());

        // [BỔ SUNG QUAN TRỌNG]: Ánh xạ các trường thông tin giao hàng
        // Giả định Entity Orders có các trường getRecipientName(), getRecipientPhone(), getShippingAddress()
        d.setRecipientName(e.getRecipientName());
        d.setRecipientPhone(e.getRecipientPhone());
        d.setShippingAddress(e.getShippingAddress());
        // [Cần thêm TotalPrice/TotalAmount vào DTO]
        // d.setTotalPrice(e.getTotalAmount());

        d.setOrderItems(
                e.getOrderPhones().stream()
                        .map(OrdersMapper::toOrdersPhonesDTO)
                        .collect(Collectors.toList())
        );

        return d;
    }
}