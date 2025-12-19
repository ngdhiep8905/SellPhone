package com.ptmhdv.SellPhone.order.controller;

import com.ptmhdv.SellPhone.order.dto.CheckoutRequest;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.mapper.OrdersMapper;
import com.ptmhdv.SellPhone.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        try {
            Orders order = orderService.checkout(request);
            // Chuyển sang DTO trước khi trả về để tránh lỗi vòng lặp JSON
            return ResponseEntity.ok(OrdersMapper.toDTO(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}