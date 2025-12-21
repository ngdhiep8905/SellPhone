package com.ptmhdv.SellPhone.user.controller;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.service.CartService;
import com.ptmhdv.SellPhone.user.dto.UserLoginResponse;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private static final String CART_TOKEN_COOKIE = "CART_TOKEN";

    @Autowired
    private AuthService authService;

    @Autowired
    private CartService cartService;

    @PostMapping("/login")
    public UserLoginResponse login(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        Users u = authService.login(email, password);

        // 1) Lấy guest token hiện tại (nếu có)
        String guestToken = getCookieValue(request, "CART_TOKEN");

        // 2) Lấy/tạo cart của user
        Cart userCart = cartService.getOrCreateCartByUser(u);

        // 3) Set cookie CART_TOKEN = token của user cart
        Cookie ck = new Cookie("CART_TOKEN", userCart.getCartToken());
        ck.setPath("/");
        ck.setMaxAge(60 * 60 * 24 * 30); // 30 ngày
        // ck.setHttpOnly(true); // bật nếu FE không cần đọc cookie
        response.addCookie(ck);

        // 4) Xóa guest cart cũ cho sạch DB (chỉ xóa nếu guest cart thật)
        cartService.deleteGuestCartByToken(guestToken);

        // ===== trả response như cũ =====
        UserLoginResponse.RoleDto roleDto = null;
        if (u.getRole() != null) {
            roleDto = new UserLoginResponse.RoleDto(u.getRole().getRoleId(), u.getRole().getRoleName());
        }

        return new UserLoginResponse(
                u.getUserId(),
                u.getEmail(),
                u.getFullName(),
                u.getPhone(),
                u.getAddress(),
                roleDto
        );
    }
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        String newToken = UUID.randomUUID().toString();

        Cookie ck = new Cookie("CART_TOKEN", newToken);
        ck.setPath("/");
        ck.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(ck);

        cartService.getOrCreateCartByToken(newToken);
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
