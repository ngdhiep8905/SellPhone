package com.ptmhdv.SellPhone.payment.controller;

import com.ptmhdv.SellPhone.order.repository.OrdersRepository;
import com.ptmhdv.SellPhone.payment.service.PayosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payos")
public class PayosWebhookController {


    private final PayosService payosService;
    private final OrdersRepository ordersRepo;

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<String> webhook(@RequestBody java.util.Map<String, Object> body) {
        System.out.println("🔥 PAYOS WEBHOOK HIT 🔥");
        System.out.println(body);

        try {
            var data = payosService.verifyWebhook(body);

            // đọc code trực tiếp từ body (an toàn theo nhiều version)
            Object codeObj = body.get("code");
            String code = codeObj == null ? null : String.valueOf(codeObj);

            if (code == null || !"00".equals(code)) {
                return ResponseEntity.ok("OK");
            }

            Long orderCode = data.getOrderCode();
            Long amount = data.getAmount();

            var opt = ordersRepo.findByPayosOrderCode(orderCode);
            if (opt.isEmpty()) return ResponseEntity.ok("OK");

            var order = opt.get();

            // idempotent theo Orders.paymentStatus (Option 1)
            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) return ResponseEntity.ok("OK");

            long expected = order.getTotalAmount().longValue();
            if (amount != null && amount.longValue() != expected) {
                return ResponseEntity.badRequest().body("Amount mismatch");
            }

            order.setPaymentStatus("PAID");


            if ("AWAITING_PAYMENT".equals(order.getStatus())) {
                order.setStatus("PROCESSING");
                // hoặc "CONFIRMED" / "PAID" tuỳ nghiệp vụ
            }

            ordersRepo.save(order);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid webhook: " + e.getMessage());
        }
    }

}
