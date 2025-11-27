package com.example.springboot_01.service;

import com.example.springboot_01.common.Result;
import com.example.springboot_01.entity.Category;

public interface ProductService {
    Result<?> queryByType(Integer categoryId);
}
