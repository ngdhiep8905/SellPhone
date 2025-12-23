package com.ptmhdv.SellPhone.payment.controller;

import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.service.PayosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payos")
public class PayosWebhookController {

    private final PayosService payosService;
    private final OrdersRepository ordersRepo;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Object body) {
        try {
            var data = payosService.verifyWebhook(body);

            Long orderCode = data.getOrderCode();
            Long amount = data.getAmount(); // VND

            var opt = ordersRepo.findByPayosOrderCode(orderCode);
            if (opt.isEmpty()) return ResponseEntity.ok("OK");

            var order = opt.get();

            // idempotent
            if ("PAID".equalsIgnoreCase(order.getStatus())) return ResponseEntity.ok("OK");

            // đối chiếu amount (khuyên dùng)
            long expected = order.getTotalPrice().longValue();
            if (amount != null && amount.longValue() != expected) {
                return ResponseEntity.badRequest().body("Amount mismatch");
            }

            // chỉ set PAID nếu đang chờ thanh toán
            if (!"AWAITING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.ok("OK");
            }

            order.setStatus("PAID");
            ordersRepo.save(order);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid webhook: " + e.getMessage());
        }
    }
}
