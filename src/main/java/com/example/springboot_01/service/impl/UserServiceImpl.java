package com.example.springboot_01.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_01.common.TokenVO;
import com.example.springboot_01.dto.UserDTO;
import com.example.springboot_01.dto.UserLoginDTO;
import com.example.springboot_01.dto.UserRegisterDTO;
import com.example.springboot_01.entity.User;
import com.example.springboot_01.mapper.UserMapper;
import com.example.springboot_01.service.UserService;
import com.example.springboot_01.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public TokenVO register(UserRegisterDTO dto) {
        User exist = this.lambdaQuery().eq(User::getUsername, dto.getUsername()).one();
        if (exist != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        this.save(user);

        String token = JwtUtils.generateToken(user.getId());
        return new TokenVO(token);
    }

    @Override
    public TokenVO login(UserLoginDTO dto) {
        User user = this.lambdaQuery().eq(User::getUsername, dto.getUsername()).one();

        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = JwtUtils.generateToken(user.getId());
        return new TokenVO(token);
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
