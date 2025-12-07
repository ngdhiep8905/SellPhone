package com.ptmhdv.sellphone.cart.service;

import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.cart.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepo;

    public List<CartItem> getItemsByCart(String cartId) {
        return cartItemRepo.findByCart_CartId(cartId);
    }

    public CartItem save(CartItem item) {
        return cartItemRepo.save(item);
    }

    public void delete(String cartId, String phoneId) {
        cartItemRepo.deleteByCart_CartIdAndPhone_PhoneId(cartId, phoneId);
    }
}
