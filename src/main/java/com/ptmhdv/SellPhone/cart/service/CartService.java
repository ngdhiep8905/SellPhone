package com.ptmhdv.SellPhone.cart.service;

import com.ptmhdv.SellPhone.cart.entity.Cart;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.common.exception.ResourceNotFoundException;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.repository.UsersRepository;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.cart.repository.CartItemRepository;
import com.ptmhdv.SellPhone.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final UsersRepository usersRepo;
    private final PhonesRepository phonesRepo;
    private final CartItemRepository cartItemRepo;

    @Autowired
    public CartService(
            CartRepository cartRepo,
            UsersRepository usersRepo,
            PhonesRepository phonesRepo,
            CartItemRepository cartItemRepo
    ) {
        this.cartRepo = cartRepo;
        this.usersRepo = usersRepo;
        this.phonesRepo = phonesRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public Cart getCartByUser(String userId) {
        return cartRepo.findByUser_UserId(userId);
    }

    public Cart addToCart(String userId, String phoneId, Integer quantity) {

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Phones phone = phonesRepo.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Phone not found"));

        Cart cart = cartRepo.findByUserUserId(userId)
                .orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepo.save(cart); // nếu fail → exception, không return null
        }

        CartItem item = cartItemRepo
                .findByCartCartIdAndPhonePhoneId(cart.getCartId(), phoneId)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setPhone(phone);
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        cartItemRepo.save(item);
        return cart;
    }

    public Cart updateQuantity(String cartItemId, Integer quantity) {

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();

        if (quantity <= 0) {
            cartItemRepo.delete(item);
            return cart;
        }

        item.setQuantity(quantity);
        cartItemRepo.save(item);
        return cart;
    }

    public Cart removeItem(String cartItemId) {

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Cart cart = item.getCart();
        cartItemRepo.delete(item);
        return cart;
    }
}
