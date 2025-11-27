package com.example.springboot_01.common;

import com.example.springboot_01.entity.User;

public class UserContextHolder {

    private static final ThreadLocal<User> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void setUser(User user) {
        CONTEXT.set(user);
    }

    public static User getUser() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}



