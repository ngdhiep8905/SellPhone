package com.ptmhdv.SellPhone.order.controller;

import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.mapper.OrdersMapper;
import com.ptmhdv.SellPhone.order.service.OrderService;
import com.ptmhdv.SellPhone.order.dto.OrdersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService ordersService;

    // POST /api/orders/checkout
    @PostMapping("/checkout")
    public OrdersDTO checkout(
            @RequestParam String userId,
            @RequestParam String receiverName,
            @RequestParam String receiverAddress,
            @RequestParam String receiverPhone,
            @RequestParam(required = false) String couponCode,
            @RequestParam String paymentId   // ĐÃ SỬA Integer → String UUID
    ) {

        Orders order = ordersService.checkout(
                userId,
                receiverName,
                receiverAddress,
                receiverPhone,
                couponCode,
                paymentId
        );

        return OrdersMapper.toDTO(order);
    }
}
