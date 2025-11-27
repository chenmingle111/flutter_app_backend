package com.example.springboot_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_01.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}