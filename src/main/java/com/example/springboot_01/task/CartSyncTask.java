package com.example.springboot_01.task;

import com.example.springboot_01.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
public class CartSyncTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IShoppingCartService shoppingCartService;

    private static final String ACTIVE_USERS_KEY = "cart:active_users";
    private static final long THIRTY_MINUTES_MILLIS = 30 * 60 * 1000L;

    @Scheduled(cron = "0 0/1 * * * ?") // Run every minute
    public void syncInactiveCarts() {
        long now = System.currentTimeMillis();
        long threshold = now - THIRTY_MINUTES_MILLIS;

        // Find users with score < threshold
        Set<Object> inactiveUsers = redisTemplate.opsForZSet().rangeByScore(ACTIVE_USERS_KEY, 0, threshold);

        if (inactiveUsers != null && !inactiveUsers.isEmpty()) {
            for (Object userIdObj : inactiveUsers) {
                String userIdStr = (String) userIdObj;
                Long userId = Long.valueOf(userIdStr);

                // Sync cart for this user
                shoppingCartService.syncCart(userId);

                // Remove from ZSet (syncCart already does this, but safe to ensure)
                // Actually syncCart removes it, so we don't need to double remove,
                // but we should be careful about concurrency.
                // Since syncCart removes it, we are good.
            }
        }
    }
}
