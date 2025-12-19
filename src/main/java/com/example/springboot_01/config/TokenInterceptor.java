package com.example.springboot_01.config;
import cn.hutool.json.JSONUtil;
import com.example.springboot_01.common.Result;
import com.example.springboot_01.common.UserContextHolder;
import com.example.springboot_01.entity.User;
import com.example.springboot_01.service.UserService;
import com.example.springboot_01.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public TokenInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = JwtUtils.parseToken(token);
            if (userId != null) {
                User currentUser = userService.getById(userId);
                if (currentUser != null) {
                    UserContextHolder.setUser(currentUser);
                    request.setAttribute("currentUserId", userId);
                    return true;
                }
            }
        }
        writeUnauthorized(response);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.fail("Token missing or invalid");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}

