package com.example.springboot_01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_01.dto.CartItemDTO;
import com.example.springboot_01.entity.Product;
import com.example.springboot_01.entity.ShoppingCart;
import com.example.springboot_01.mapper.ProductMapper;
import com.example.springboot_01.mapper.ShoppingCartMapper;
import com.example.springboot_01.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements IShoppingCartService {

    @Autowired
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductMapper productMapper;

    private static final String CART_KEY_PREFIX = "cart:user:";
    private static final String ACTIVE_USERS_KEY = "cart:active_users";

    @Override
    public void addToCart(Long userId, CartItemDTO cartItemDTO) {
        String key = CART_KEY_PREFIX + userId;
        String productId = String.valueOf(cartItemDTO.getId());
        Integer quantity = cartItemDTO.getItem();

        // Update Redis Hash
        redisTemplate.opsForHash().put(key, productId, quantity);

        // Update Active User ZSet (score = current timestamp)
        redisTemplate.opsForZSet().add(ACTIVE_USERS_KEY, String.valueOf(userId), System.currentTimeMillis());
    }

    @Override
    public void updateCartItem(Long userId, CartItemDTO cartItemDTO) {
        addToCart(userId, cartItemDTO); // Same logic for update
    }

    @Override
    public void removeCartItem(Long userId, Long productId) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForHash().delete(key, String.valueOf(productId));

        // Update Active User ZSet
        redisTemplate.opsForZSet().add(ACTIVE_USERS_KEY, String.valueOf(userId), System.currentTimeMillis());
    }

    @Override
    public List<CartItemDTO> getCartItems(Long userId) {
        String key = CART_KEY_PREFIX + userId;

        // 1. Try to get from Redis
        java.util.Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            // 2. If empty in Redis, load from MySQL (optional, but good for consistency)
            // For this requirement, we assume if Redis is empty, we might need to load from
            // DB or return empty.
            // Let's load from DB to be safe and populate Redis?
            // Or just return DB results directly without populating Redis (lazy load)?
            // Requirement says "not directly update mysql", implying Redis is the write
            // buffer.
            // But if user comes back after 30 mins, data is in MySQL.
            // So we should load from MySQL if Redis is empty.
            return loadFromMysql(userId);
        }

        List<CartItemDTO> result = new ArrayList<>();
        for (java.util.Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long productId = Long.valueOf((String) entry.getKey());
            Integer quantity = (Integer) entry.getValue();

            Product product = productMapper.selectById(productId);
            if (product != null) {
                CartItemDTO dto = new CartItemDTO();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setImage(product.getImage());
                dto.setItem(quantity);
                dto.setTotalPrice(product.getPrice() * quantity);
                result.add(dto);
            }
        }

        // Update Active User ZSet
        redisTemplate.opsForZSet().add(ACTIVE_USERS_KEY, String.valueOf(userId), System.currentTimeMillis());

        return result;
    }

    private List<CartItemDTO> loadFromMysql(Long userId) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartItems = this.list(queryWrapper);

        List<CartItemDTO> result = new ArrayList<>();
        if (cartItems != null && !cartItems.isEmpty()) {
            String key = CART_KEY_PREFIX + userId;
            for (ShoppingCart item : cartItems) {
                // Populate Redis
                redisTemplate.opsForHash().put(key, String.valueOf(item.getProductId()), item.getItem());

                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setImage(product.getImage());
                    dto.setItem(item.getItem());
                    dto.setTotalPrice(product.getPrice() * item.getItem());
                    result.add(dto);
                }
            }
            // Update Active User ZSet
            redisTemplate.opsForZSet().add(ACTIVE_USERS_KEY, String.valueOf(userId), System.currentTimeMillis());
        }
        return result;
    }

    @Override
    public void syncCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        java.util.Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries != null && !entries.isEmpty()) {
            // 1. Delete existing cart in MySQL for this user (Simplest sync strategy:
            // replace all)
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getUserId, userId);
            this.remove(queryWrapper);

            // 2. Insert new items
            List<ShoppingCart> entityList = new ArrayList<>();
            for (java.util.Map.Entry<Object, Object> entry : entries.entrySet()) {
                Long productId = Long.valueOf((String) entry.getKey());
                Integer quantity = (Integer) entry.getValue();

                ShoppingCart cart = new ShoppingCart();
                cart.setUserId(userId);
                cart.setProductId(productId);
                cart.setItem(quantity);
                entityList.add(cart);
            }
            this.saveBatch(entityList);

            // 3. Keep Redis cache (do not delete)
        }

        // 4. Remove from Active User ZSet
        redisTemplate.opsForZSet().remove(ACTIVE_USERS_KEY, String.valueOf(userId));
    }

    @Override
    public void addShoppingCart(Long userId, CartItemDTO cartItemDTO) {
        // Only add to Redis cache, no MySQL operation
        addToCart(userId, cartItemDTO);
    }
}
