package com.example.springboot_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_01.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}




