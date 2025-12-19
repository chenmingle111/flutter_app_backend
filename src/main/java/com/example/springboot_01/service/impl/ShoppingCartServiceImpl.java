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

import java.time.Duration;
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

        if (quantity == null || quantity <= 0) {
            // 0 或负数：视为删除该商品
            redisTemplate.opsForHash().delete(key, productId);
        } else {
            // 累加数量（原逻辑为覆盖，这里改为累加）
            Integer existingQty = (Integer) redisTemplate.opsForHash().get(key, productId);
            int newQty = (existingQty == null ? 0 : existingQty) + quantity;
            redisTemplate.opsForHash().put(key, productId, newQty);
        }

        // 更新用户活跃度（时间戳越大越靠后）
        redisTemplate.opsForZSet().add(ACTIVE_USERS_KEY, String.valueOf(userId), System.currentTimeMillis());

        // 设置购物车过期时间（7天，可选）
        redisTemplate.expire(key, Duration.ofDays(7));
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