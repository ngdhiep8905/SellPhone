package com.ptmhdv.sellphone.cart.service;

import com.ptmhdv.sellphone.cart.entity.Cart;
import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.catalog.entity.Phones;
import com.ptmhdv.sellphone.common.exception.ResourceNotFoundException;
import com.ptmhdv.sellphone.user.entity.Users;
import com.ptmhdv.sellphone.user.repository.UsersRepository;
import com.ptmhdv.sellphone.catalog.repository.PhonesRepository;
import com.ptmhdv.sellphone.cart.repository.CartItemRepository;
import com.ptmhdv.sellphone.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;
    private UsersRepository usersRepo;
    private PhonesRepository phonesRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    public Cart getCartByUser(String userId) {
        return cartRepo.findByUser_UserId(userId);
    }

    public Cart createCart(Users user) {
        Cart cart = new Cart(user);
        return cartRepo.save(cart);
    }

    public Cart addToCart(String userId, String phoneId, Integer quantity) {

        // 1. Lấy user
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Lấy sản phẩm
        Phones phone = phonesRepo.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        // 3. Lấy cart của user (nếu chưa có thì tạo mới)
        Cart cart = cartRepo.findByUserUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepo.save(c);
                });

        // 4. Kiểm tra xem trong cart đã có sản phẩm này chưa
        CartItem item = cartItemRepo.findByCartCartIdAndPhonePhoneId(cart.getCartId(), phoneId)
                .orElse(null);

        if (item == null) {
            // 5. Nếu chưa có → tạo mới CartItem
            item = new CartItem();
            item.setCart(cart);
            item.setPhone(phone);
            item.setQuantity(quantity);
        } else {
            // 6. Nếu đã có → tăng số lượng
            item.setQuantity(item.getQuantity() + quantity);
        }

        cartItemRepo.save(item);

        return cart;
    }

    public Cart updateQuantity(String cartItemId, Integer quantity) {
        CartItem item = cartItemRepo.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        // 2. Nếu quantity = 0 → xóa item khỏi giỏ
        if (quantity == 0) {
            cartItemRepo.delete(item);
            return cart; // giỏ sau khi xóa item
        }

        // 3. Cập nhật số lượng mới
        item.setQuantity(quantity);
        cartItemRepo.save(item);

        return cart;
    }

    public Cart removeItem(String cartItemId) {

        // 1. Lấy cart item
        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // 2. Lấy cart tương ứng
        Cart cart = item.getCart();

        // 3. Xóa item khỏi repository
        cartItemRepo.delete(item);

        // 4. Trả về cart sau khi xóa item
        return cart;
    }

}
