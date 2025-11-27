package com.example.springboot_01.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_01.common.Result;
import com.example.springboot_01.entity.Category;

public interface CategoryService extends IService<Category> {
    Result queryByList();


    Result queryByType(Integer id);

    Result queryIntroById(Integer id);
}
