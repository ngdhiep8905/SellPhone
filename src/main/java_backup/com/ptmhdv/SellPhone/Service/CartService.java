package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Cart;
import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Repository.CartItemRepository;
import com.ptmhdv.SellPhone.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    public Cart getCartByUser(String userId) {
        return cartRepo.findByUser_UserId(userId);
    }

    public Cart createCart(Users user) {
        Cart cart = new Cart(user);
        return cartRepo.save(cart);
    }
}
