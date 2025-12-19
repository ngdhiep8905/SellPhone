package com.ptmhdv.SellPhone.order.mapper;

import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.dto.OrdersDTO;
import com.ptmhdv.SellPhone.order.dto.OrdersPhonesDTO;

import java.util.stream.Collectors;

public class OrdersMapper {

    public static OrdersDTO toDTO(Orders e) {
        if (e == null) return null;

        OrdersDTO d = new OrdersDTO();
        d.setId(e.getOrderId());
        d.setUserId(e.getUser() != null ? e.getUser().getUserId() : null);
        d.setStatus(e.getStatus());
        d.setPaymentId(e.getPayment() != null ? e.getPayment().getPaymentId() : null);

        // Thông tin giao hàng
        d.setRecipientName(e.getRecipientName());
        d.setRecipientPhone(e.getRecipientPhone());
        d.setShippingAddress(e.getShippingAddress());
        d.setTotalPrice(e.getTotalPrice()); // Khớp với Entity totalPrice

        // Chuyển đổi danh sách chi tiết sản phẩm
        if (e.getOrderPhones() != null) {
            d.setOrderItems(e.getOrderPhones().stream()
                    .map(OrdersMapper::toOrdersPhonesDTO)
                    .collect(Collectors.toList()));
        }

        return d;
    }

    public static OrdersPhonesDTO toOrdersPhonesDTO(OrdersPhones e) {
        if (e == null) return null;

        OrdersPhonesDTO d = new OrdersPhonesDTO();

        // Xử lý lấy ID từ EmbeddedId OrdersPhonesId
        if (e.getId() != null) {
            d.setOrderId(e.getId().getOrder());
            d.setPhoneId(e.getId().getPhone());
        }

        d.setQuantity(e.getQuantity());
        d.setPrice(e.getPrice());
        // Trả thêm phoneName nếu DTO có trường này giúp FE hiển thị dễ hơn
        if (e.getPhone() != null) {
            // d.setPhoneName(e.getPhone().getPhoneName());
        }

        return d;
    }
}