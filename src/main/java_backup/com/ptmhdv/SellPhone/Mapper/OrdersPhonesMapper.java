package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.OrdersPhones;
import com.ptmhdv.SellPhone.dto.OrdersPhonesDTO;

public class OrdersPhonesMapper {
    public static OrdersPhonesDTO toDTO(OrdersPhones e) {
        if (e == null) return null;

        OrdersPhonesDTO d = new OrdersPhonesDTO();
        // Nếu id trong entity là ordersPhonesId thì sửa lại cho đúng
        d.setId(e.getOrderPhoneId());
        d.setOrderId(e.getOrder().getOrderId());
        d.setPhoneId(e.getPhone().getPhoneId()); // bạn đang dùng getPhoneId()
        d.setQuantity(e.getQuantity());
        return d;
    }
}
