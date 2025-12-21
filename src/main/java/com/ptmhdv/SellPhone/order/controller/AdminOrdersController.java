package com.ptmhdv.SellPhone.order.controller;

import com.ptmhdv.SellPhone.order.dto.OrderAdminDetailDTO;
import com.ptmhdv.SellPhone.order.dto.OrderAdminRowDTO;
import com.ptmhdv.SellPhone.order.service.AdminOrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@CrossOrigin
public class AdminOrdersController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public List<OrderAdminRowDTO> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus
    ) {
        return adminOrderService.list(status, paymentMethod, paymentStatus);
    }

    @GetMapping("/{id}")
    public OrderAdminDetailDTO detail(@PathVariable String id) {
        return adminOrderService.detail(id);
    }

    @PutMapping("/{id}/confirm")
    public void confirm(@PathVariable String id) {
        adminOrderService.confirm(id);
    }

    @PutMapping("/{id}/delivered")
    public void delivered(@PathVariable String id) {
        adminOrderService.delivered(id);
    }

    @PutMapping("/{id}/reject")
    public void reject(@PathVariable String id, @RequestBody RejectReq req) {
        adminOrderService.reject(id, req.getReason());
    }

    @Data
    public static class RejectReq {
        private String reason;
    }
}
