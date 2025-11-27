package com.example.springboot_01.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_01.common.TokenVO;
import com.example.springboot_01.dto.UserDTO;
import com.example.springboot_01.dto.UserLoginDTO;
import com.example.springboot_01.dto.UserRegisterDTO;
import com.example.springboot_01.entity.User;

public interface UserService extends IService<User> {
    TokenVO register(UserRegisterDTO dto);

    TokenVO login(UserLoginDTO dto);

    UserDTO getUserInfo(Long userId);
}
