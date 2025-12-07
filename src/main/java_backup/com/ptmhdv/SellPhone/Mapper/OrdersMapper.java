package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.Orders;
import com.ptmhdv.SellPhone.Entity.OrdersPhones;
import com.ptmhdv.SellPhone.dto.OrdersDTO;
import com.ptmhdv.SellPhone.dto.OrdersPhonesDTO;

import java.util.stream.Collectors;

public class OrdersMapper {
    public static OrdersPhonesDTO toOrdersPhonesDTO(OrdersPhones e) {
        OrdersPhonesDTO d = new OrdersPhonesDTO();
        d.setId(e.getOrderPhoneId());
        d.setOrderId(e.getOrder().getOrderId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());
        d.setPrice(e.getPrice());
        return d;
    }

    public static OrdersDTO toDTO(Orders e) {
        OrdersDTO d = new OrdersDTO();
        d.setId(e.getOrderId());
        d.setUserId(e.getUser().getUserId());
        d.setStatus(e.getStatus());
        if (e.getPayment() != null)
            d.setPaymentId(e.getPayment().getPaymentId());

        d.setOrderItems(
                e.getOrderPhones().stream()
                        .map(OrdersMapper::toOrdersPhonesDTO)
                        .collect(Collectors.toList())
        );

        return d;
    }
}
