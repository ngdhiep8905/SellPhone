package com.ptmhdv.sellphone.cart.controller;

import com.ptmhdv.sellphone.cart.entity.Cart;
import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.catalog.mapper.PhonesMapper;
import com.ptmhdv.sellphone.cart.service.CartService;
import com.ptmhdv.sellphone.cart.service.CartItemService;
import com.ptmhdv.sellphone.cart.dto.CartItemViewDTO;
import com.ptmhdv.sellphone.cart.dto.CartViewDTO;
import com.ptmhdv.sellphone.catalog.dto.PhonesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    // GET /api/cart/{userId}
    @GetMapping("/{userId}")
    public CartViewDTO getCart(@PathVariable String userId) {
        Cart cart = cartService.getCartByUser(userId);
        return toView(cart);
    }

    // POST /api/cart/items?userId=&phoneId=&quantity=
    @PostMapping("/items")
    public CartViewDTO addToCart(
            @RequestParam String userId,
            @RequestParam String phoneId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.addToCart(userId, phoneId, quantity);
        return toView(cart);
    }

    // PUT /api/cart/items/{cartItemId}?quantity=...
    @PutMapping("/items/{cartItemId}")
    public CartViewDTO updateItem(
            @PathVariable String cartItemId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.updateQuantity(cartItemId, quantity);
        return toView(cart);
    }

    // DELETE /api/cart/items/{cartItemId}
    @DeleteMapping("/items/{cartItemId}")
    public CartViewDTO removeItem(@PathVariable String cartItemId) {
        Cart cart = cartService.removeItem(cartItemId);
        return toView(cart);
    }

    // ====== mapping từ Entity sang ViewDTO mà FE cần ======
    private CartViewDTO toView(Cart cart) {
        CartViewDTO dto = new CartViewDTO();
        dto.setCartId(cart.getCartId());

        List<CartItemViewDTO> items = cart.getItems().stream()
                .map(this::toItemView)
                .collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    private CartItemViewDTO toItemView(CartItem item) {
        CartItemViewDTO dto = new CartItemViewDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setQuantity(item.getQuantity()); // ĐÃ SỬA quantityPrice → quantity

        PhonesDTO phoneDto = PhonesMapper.toDTO(item.getPhone());
        dto.setPhone(phoneDto);

        return dto;
    }
}
