package com.ptmhdv.SellPhone.cart.controller;

import com.ptmhdv.SellPhone.cart.dto.CartItemViewDTO;
import com.ptmhdv.SellPhone.cart.dto.CartViewDTO;
import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.cart.service.CartService;
import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.mapper.PhonesMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String CART_TOKEN_COOKIE = "CART_TOKEN";
    private static final int CART_TOKEN_MAX_AGE = 60 * 60 * 24 * 30; // 30 days

    private final CartService cartService;

    // GET /api/cart
    @GetMapping
    public CartViewDTO getCart(HttpServletRequest req, HttpServletResponse res) {
        String token = ensureCartToken(req, res);
        Cart cart = cartService.getOrCreateCartByToken(token);
        return toView(cart);
    }

    // POST /api/cart/items?phoneId=&quantity=
    @PostMapping("/items")
    public CartViewDTO addToCart(
            @RequestParam String phoneId,
            @RequestParam Integer quantity,
            HttpServletRequest req,
            HttpServletResponse res) {

        String token = ensureCartToken(req, res);
        Cart cart = cartService.addToCartByToken(token, phoneId, quantity);
        return toView(cart);
    }

    // PUT /api/cart/items/{cartItemId}?quantity=...
    @PutMapping("/items/{cartItemId}")
    public CartViewDTO updateItem(
            @PathVariable String cartItemId,
            @RequestParam Integer quantity,
            HttpServletRequest req) {

        String token = getCartToken(req);
        Cart cart = cartService.updateQuantityByToken(token, cartItemId, quantity);
        return toView(cart);
    }

    // DELETE /api/cart/items/{cartItemId}
    @DeleteMapping("/items/{cartItemId}")
    public CartViewDTO removeItem(
            @PathVariable String cartItemId,
            HttpServletRequest req) {

        String token = getCartToken(req);
        Cart cart = cartService.removeItemByToken(token, cartItemId);
        return toView(cart);
    }

    // ===================== Token helpers =====================

    private String ensureCartToken(HttpServletRequest req, HttpServletResponse res) {
        String token = getCartToken(req);
        if (token == null || token.isBlank()) {
            token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(CART_TOKEN_COOKIE, token);
            cookie.setPath("/");
            cookie.setMaxAge(CART_TOKEN_MAX_AGE);
            cookie.setHttpOnly(false); // FE có thể không cần đọc; nhưng để đơn giản cứ false
            res.addCookie(cookie);
        }
        return token;
    }

    private String getCartToken(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (CART_TOKEN_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    // ===================== Mapping Entity -> ViewDTO =====================

    private CartViewDTO toView(Cart cart) {
        CartViewDTO dto = new CartViewDTO();
        if (cart == null) {
            dto.setItems(List.of());
            return dto;
        }

        dto.setCartId(cart.getCartId());

        List<CartItemViewDTO> items = (cart.getItems() == null)
                ? List.of()
                : cart.getItems().stream()
                .map(this::toItemView)
                .collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    private CartItemViewDTO toItemView(CartItem item) {
        CartItemViewDTO dto = new CartItemViewDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setQuantity(item.getQuantity());

        PhonesDTO phoneDto = PhonesMapper.toDTO(item.getPhone());
        dto.setPhone(phoneDto);

        return dto;
    }
}
