package com.example.springboot_01.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_01.dto.CartItemDTO;
import com.example.springboot_01.entity.ShoppingCart;

import java.util.List;

public interface IShoppingCartService extends IService<ShoppingCart> {
    /**
     * Add item to cart (Redis)
     */
    void addToCart(Long userId, CartItemDTO cartItemDTO);

    /**
     * Update cart item quantity (Redis)
     */
    void updateCartItem(Long userId, CartItemDTO cartItemDTO);

    /**
     * Remove item from cart (Redis)
     */
    void removeCartItem(Long userId, Long productId);

    /**
     * Sync Redis cart to MySQL
     */
    void syncCart(Long userId);

    List<CartItemDTO> getCartItems(Long userId);

    /**
     * Add item to cart (Redis only)
     */
    void addShoppingCart(Long userId, CartItemDTO cartItemDTO);
}
