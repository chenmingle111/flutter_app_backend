package com.example.springboot_01.controller;

import com.example.springboot_01.common.*;
import com.example.springboot_01.dto.UserDTO;
import com.example.springboot_01.dto.UserLoginDTO;
import com.example.springboot_01.dto.UserRegisterDTO;
import com.example.springboot_01.service.UserService;
import com.example.springboot_01.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ---------------------------
    // 1️⃣ 注册
    // ---------------------------
    @PostMapping("/register")
    public Result<TokenVO> register(@RequestBody UserRegisterDTO dto) {
        TokenVO tokenVO = userService.register(dto);
        return Result.ok("Registration successful", tokenVO);
    }

    // ---------------------------
    // 2️⃣ 登录
    // ---------------------------
    @PostMapping("/login")
    public Result<TokenVO> login(@RequestBody UserLoginDTO dto) {
        TokenVO tokenVO = userService.login(dto);
        return Result.ok("Login successful", tokenVO);
    }

    // ---------------------------
    // 3️⃣ 获取当前用户信息
    // ---------------------------
    @GetMapping("/me")
    public Result<UserDTO> me(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Result.fail("Missing token");
        }

        String token = auth.substring(7);
        Long userId = JwtUtils.parseToken(token);
        if (userId == null) {
            return Result.fail("Invalid or expired token");
        }

        UserDTO userDTO = userService.getUserInfo(userId);
        return Result.ok(userDTO);
    }
}
