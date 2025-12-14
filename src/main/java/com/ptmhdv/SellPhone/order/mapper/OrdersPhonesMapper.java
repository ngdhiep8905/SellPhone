package com.ptmhdv.SellPhone.order.mapper;

import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.dto.OrdersPhonesDTO;

public class OrdersPhonesMapper {
    public static OrdersPhonesDTO toDTO(OrdersPhones e) {
        if (e == null) return null;

        OrdersPhonesDTO d = new OrdersPhonesDTO();

        // 1. [SỬA LỖI TRUY CẬP ID]: Truy cập đối tượng Khóa tổng hợp (OrdersPhonesId)
        // Lưu ý: Nếu OrdersPhonesDTO của bạn chỉ có trường đơn giản (orderId, phoneId),
        // bạn cần lấy các thành phần từ Khóa tổng hợp.

        if (e.getId() != null) {
            // Lấy orderId từ Khóa tổng hợp
            d.setOrderId(e.getId().getOrder());
            // Lấy phoneId từ Khóa tổng hợp
            d.setPhoneId(e.getId().getPhone());
        } else {
            // Trường hợp Entity OrdersPhones không có ID (thường là lỗi)
            // Nếu Entity OrderDetails không có trường OrderDetailId, bạn không cần dòng này
            // d.setOrderDetailId(null);
        }
        d.setOrderId(e.getOrder().getOrderId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());
        return d;
    }
}
