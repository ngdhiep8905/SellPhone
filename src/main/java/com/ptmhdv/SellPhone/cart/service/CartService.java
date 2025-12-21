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

    /**
     * Lấy cart theo token; nếu chưa có thì tạo mới (guest cart).
     */
    @Transactional
    public Cart getOrCreateCartByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }
        return cartRepo.findByCartToken(token)
                .orElseGet(() -> createGuestCart(token));
    }

    /**
     * Thêm sản phẩm vào giỏ theo token (không cần login).
     */
    @Transactional
    public Cart addToCartByToken(String token, String phoneId, Integer quantity) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }
        if (phoneId == null || phoneId.isBlank()) {
            throw new IllegalArgumentException("phoneId is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        Phones phone = phonesRepo.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        Cart cart = getOrCreateCartByToken(token);

        CartItem item = cartItemRepo
                .findByCartCartIdAndPhonePhoneId(cart.getCartId(), phoneId)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCartItemId("CI" + System.currentTimeMillis());
            item.setCart(cart);
            item.setPhone(phone);
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        cartItemRepo.save(item);
        return cart;
    }

    /**
     * Update quantity nhưng phải đảm bảo cartItem thuộc đúng token.
     * quantity <= 0 => xóa item.
     */
    @Transactional
    public Cart updateQuantityByToken(String token, String cartItemId, Integer quantity) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("cartToken is required");
        }

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        if (cart == null || cart.getCartToken() == null || !cart.getCartToken().equals(token)) {
            // Không cho phép sửa item của cart khác
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

    /**
     * Xóa item nhưng phải đảm bảo cartItem thuộc đúng token.
     */
    @Transactional
    public Cart removeItemByToken(String token, String cartItemId) {
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

    /**
     * Tạo cart guest mới.
     * cartId: giữ format bạn đang dùng (VARCHAR(50) OK).
     */
    private Cart createGuestCart(String token) {
        Cart cart = new Cart();
        cart.setCartId("C" + System.currentTimeMillis());
        cart.setCartToken(token);
        cart.setUser(null); // guest

        return cartRepo.save(cart);
    }
    @Transactional
    public Cart attachGuestCartToUser(String token, Users user) {
        if (user == null) return null;

        // 1) Nếu user đã có cart => kiểu A: giữ nguyên, không merge
        Optional<Cart> userCartOpt = cartRepo.findByUser_UserId(user.getUserId());
        if (userCartOpt.isPresent()) {
            return userCartOpt.get();
        }

        // 2) Không có token => không có guest cart để attach
        if (token == null || token.isBlank()) return null;

        // 3) Lấy guest cart theo token
        Cart guestCart = cartRepo.findByCartToken(token).orElse(null);
        if (guestCart == null) return null;

        // 4) Nếu guestCart đã gắn user rồi:
        //    - Nếu đúng user hiện tại => OK
        //    - Nếu user khác => không attach (an toàn)
        if (guestCart.getUser() != null) {
            if (guestCart.getUser().getUserId() != null
                    && guestCart.getUser().getUserId().equals(user.getUserId())) {
                return guestCart;
            }
            return null;
        }

        // 5) Attach
        guestCart.setUser(user);
        return cartRepo.save(guestCart);
    }

    @Transactional
    public Cart getOrCreateCartByUser(Users user) {
        return cartRepo.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCartId("C" + System.currentTimeMillis());
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

        // Chỉ xóa nếu đúng guest cart (chưa attach user)
        if (guest.getUser() != null) return;

        // Xóa cart (orphanRemoval/cascade sẽ xóa cart_item)
        cartRepo.delete(guest);
    }

}
