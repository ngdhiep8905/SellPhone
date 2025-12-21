package com.ptmhdv.SellPhone.order.controller;

import com.ptmhdv.SellPhone.order.dto.CheckoutRequest;
import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.mapper.OrdersMapper;
import com.ptmhdv.SellPhone.order.service.OrderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

public class OrderController {

    private static final String CART_TOKEN_COOKIE = "CART_TOKEN";
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request,
                                      HttpServletRequest httpRequest) {
        try {
            String token = getCookieValue(httpRequest, "CART_TOKEN");
            Orders order = orderService.checkoutGuest(token, request);
            return ResponseEntity.ok(OrdersMapper.toDTO(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
