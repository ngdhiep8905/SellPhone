package com.ptmhdv.SellPhone.payment.controller;

import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.service.OrderService;
import com.ptmhdv.SellPhone.payment.dto.PaymentDTO;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {
    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }
    @PostMapping("/order/{orderId}")
    public String processOrderPayment(@PathVariable String orderId,
                                      @Valid @ModelAttribute("payment") PaymentDTO dto,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (result.hasErrors()) {
            Orders order = orderService.getOrderById(String.valueOf(orderId));
            model.addAttribute("order", order);
            return "payment/order-payment";
        }

        try {
            paymentService.processOrderPayment(orderId, dto);
            redirectAttributes.addFlashAttribute("success", "Payment processed successfully");
            return "redirect:/orders/" + orderId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payment/order/" + orderId;
        }
    }
    @PostMapping("/cod/{orderId}")
    @ResponseBody
    public ResponseEntity<?> processCODPayment(@PathVariable String orderId) {
        try {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentMethod("COD");
            paymentDTO.setTransactionId("COD-" + System.currentTimeMillis());

            paymentService.processOrderPayment(orderId, paymentDTO);

            // Update order status to CONFIRMED
            orderService.updateOrderStatus(orderId, "CONFIRMED");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đơn hàng đã được xác nhận. Thanh toán khi nhận hàng.");
            response.put("paymentMethod", "COD");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // VNPay Create QR Code API (Fake)
    @PostMapping("/vnpay/create-qr/{orderId}")
    @ResponseBody
    public ResponseEntity<?> createVNPayQR(@PathVariable String orderId) {
        try {
            Orders order = orderService.getOrderById(orderId);

            // Generate fake transaction ID
            String transactionId = "VNP" + System.currentTimeMillis();

            // Create QR data (fake VNPay QR format)
            String qrData = String.format(
                    "VNPay|%s|%s|%s|https://vnshop.vn",
                    transactionId,
                    order.getOrderId(),
                    order.getTotalPrice()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("amount", order.getTotalPrice());
            response.put("transactionId", transactionId);
            response.put("qrData", qrData);
            response.put("message", "QR Code đã được tạo");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // VNPay Confirm Payment API (Fake)
    @PostMapping("/vnpay/confirm/{orderId}")
    @ResponseBody
    public ResponseEntity<?> confirmVNPayPayment(@PathVariable String orderId, @RequestBody Map<String, Object> paymentData) {
        try {
            String transactionId = (String) paymentData.get("transactionId");

            if (transactionId == null || transactionId.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Mã giao dịch không hợp lệ");
                return ResponseEntity.badRequest().body(error);
            }

            // Fake VNPay payment confirmation
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentMethod("VNPAY");
            paymentDTO.setTransactionId(transactionId);

            paymentService.processOrderPayment(orderId, paymentDTO);

            // Update order status to CONFIRMED
            orderService.updateOrderStatus(orderId, "CONFIRMED");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thanh toán VNPay thành công");
            response.put("paymentMethod", "VNPAY");
            response.put("transactionId", transactionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

