package com.ptmhdv.sellphone.order.mapper;

import com.ptmhdv.sellphone.order.entity.OrdersPhones;
import com.ptmhdv.sellphone.order.dto.OrdersPhonesDTO;

public class OrdersPhonesMapper {
    public static OrdersPhonesDTO toDTO(OrdersPhones e) {
        if (e == null) return null;

        OrdersPhonesDTO d = new OrdersPhonesDTO();
        // Nếu id trong entity là ordersPhonesId thì sửa lại cho đúng
        d.setId(e.getOrderDetailId());
        d.setOrderId(e.getOrder().getOrderId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());
        return d;
    }
}
