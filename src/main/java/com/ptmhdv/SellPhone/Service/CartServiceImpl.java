package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Cart;
import com.ptmhdv.SellPhone.Entity.CartItem;
import com.ptmhdv.SellPhone.Entity.Phones;
import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Repository.CartItemRepository;
import com.ptmhdv.SellPhone.Repository.CartRepository;
import com.ptmhdv.SellPhone.Repository.PhonesRepository;
import com.ptmhdv.SellPhone.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;

    private Cart getOrCreateCart(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart getCartByUser(Long userId) {
        return getOrCreateCart(userId);
    }

    @Override
    public Cart addToCart(Long userId, Long phoneId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Phones phone = phonesRepository.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        CartItem target = cart.getItems().stream()
                .filter(ci -> ci.getPhone().getPhoneId().equals(phoneId))
                .findFirst()
                .orElse(null);

        if (target == null) {
            target = new CartItem();
            target.setCart(cart);
            target.setPhone(phone);
            target.setQuantityPrice(0);
            cart.getItems().add(target);
        }

        target.setQuantityPrice(target.getQuantityPrice() + quantity);
        cartItemRepository.save(target);

        return cart;
    }

    @Override
    public Cart updateQuantity(Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return item.getCart();
        }

        item.setQuantityPrice(quantity);
        cartItemRepository.save(item);

        return item.getCart();
    }

    @Override
    public Cart removeItem(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = item.getCart();
        cartItemRepository.delete(item);

        return cart;
    }
}
