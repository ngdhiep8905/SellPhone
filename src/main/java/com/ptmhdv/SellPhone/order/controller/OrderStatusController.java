package com.ptmhdv.SellPhone.order.controller;

import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderStatusController {

    private final OrdersRepository ordersRepo;

    @GetMapping("/{orderId}/status")
    public ResponseEntity<?> getStatus(@PathVariable String orderId) {
        return ordersRepo.findById(orderId)
                .map(o -> ResponseEntity.ok(Map.of("status", o.getStatus())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
