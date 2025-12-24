package com.ptmhdv.SellPhone.cart.service;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository;
import com.ptmhdv.SellPhone.cart.repository.CartRepository;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.common.exception.ResourceNotFoundException;
import com.ptmhdv.SellPhone.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final PhonesRepository phonesRepo;
    private final CartItemRepository cartItemRepo;

    @Autowired
    public CartService(
            CartRepository cartRepo,
            PhonesRepository phonesRepo,
            CartItemRepository cartItemRepo
    ) {
        this.cartRepo = cartRepo;
        this.phonesRepo = phonesRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @Transactional
    public Cart getOrCreateCartByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }
        return cartRepo.findByCartToken(token)
                .orElseGet(() -> createGuestCart(token));
    }

    @Transactional
    public Cart addToCartByToken(String cartToken, String phoneId, int quantity) {
        Cart cart = cartRepo.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        Phones phone = phonesRepo.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        CartItem existing = cartItemRepo
                .findByCart_CartIdAndPhone_PhoneId(cart.getCartId(), phoneId)
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemRepo.save(existing);
        } else {
            CartItem ci = new CartItem();
            ci.setCart(cart);          // ✅ set relation
            ci.setPhone(phone);
            ci.setQuantity(quantity);
            cartItemRepo.save(ci);     // ✅ DB tự AUTO_INCREMENT cart_item_id
        }

        return getOrCreateCartByToken(cartToken);

    }


    @Transactional
    public Cart updateQuantityByToken(String token, Integer cartItemId, Integer quantity) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        if (cart == null || cart.getCartToken() == null || !cart.getCartToken().equals(token)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        if (quantity == null || quantity <= 0) {
            cartItemRepo.delete(item);
            return cart;
        }

        item.setQuantity(quantity);
        cartItemRepo.save(item);
        return cart;
    }

    @Transactional
    public Cart removeItemByToken(String token, Integer cartItemId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        if (cart == null || cart.getCartToken() == null || !cart.getCartToken().equals(token)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartItemRepo.delete(item);
        return cart;
    }

    private Cart createGuestCart(String token) {
        Cart cart = new Cart();

        cart.setCartToken(token);
        cart.setUser(null);
        return cartRepo.save(cart); // ✅ @PrePersist sinh cartId UUID(36)
    }


    @Transactional
    public Cart attachGuestCartToUser(String token, Users user) {
        if (user == null) return null;

        Optional<Cart> userCartOpt = cartRepo.findByUser_UserId(user.getUserId());
        if (userCartOpt.isPresent()) {
            return userCartOpt.get();
        }

        if (token == null || token.isBlank()) return null;

        Cart guestCart = cartRepo.findByCartToken(token).orElse(null);
        if (guestCart == null) return null;

        if (guestCart.getUser() != null) {
            if (guestCart.getUser().getUserId() != null
                    && guestCart.getUser().getUserId().equals(user.getUserId())) {
                return guestCart;
            }
            return null;
        }

        guestCart.setUser(user);

        // Lưu ý: Flow hiện tại của bạn đang chọn “Cách A” (login dùng cart user, xoá guest),
        // nên attachGuestCartToUser có thể không dùng tới.
        return cartRepo.save(guestCart);
    }

    @Transactional
    public Cart getOrCreateCartByUser(Users user) {
        return cartRepo.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setCartToken(UUID.randomUUID().toString());
                    return cartRepo.save(c);
                });
    }

    @Transactional
    public void deleteGuestCartByToken(String token) {
        if (token == null || token.isBlank()) return;

        Cart guest = cartRepo.findByCartToken(token).orElse(null);
        if (guest == null) return;

        if (guest.getUser() != null) return;

        cartRepo.delete(guest);
    }
}
