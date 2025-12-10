package com.ptmhdv.sellphone.order.mapper;

import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.entity.OrdersPhones;
import com.ptmhdv.sellphone.order.dto.OrdersDTO;
import com.ptmhdv.sellphone.order.dto.OrdersPhonesDTO;

import java.util.stream.Collectors;

public class OrdersMapper {
    public static OrdersPhonesDTO toOrdersPhonesDTO(OrdersPhones e) {
        OrdersPhonesDTO d = new OrdersPhonesDTO();
        d.setId(e.getOrderDetailId());
        d.setOrderId(e.getOrder().getOrderId());
        d.setPhoneId(e.getPhone().getPhoneId());
        d.setQuantity(e.getQuantity());
        d.setPrice(e.getPrice());
        d.setPrice(e.getTotalPrice());
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
