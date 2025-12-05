package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Entity.Orders;
import com.ptmhdv.SellPhone.Mapper.OrdersMapper;
import com.ptmhdv.SellPhone.Service.OrdersService;
import com.ptmhdv.SellPhone.dto.OrdersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    // POST /api/orders/checkout?...
    @PostMapping("/checkout")
    public OrdersDTO checkout(@RequestParam Long userId,
                              @RequestParam String receiverName,
                              @RequestParam String receiverAddress,
                              @RequestParam String receiverPhone,
                              @RequestParam(required = false) String couponCode,
                              @RequestParam Integer paymentId) {
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
