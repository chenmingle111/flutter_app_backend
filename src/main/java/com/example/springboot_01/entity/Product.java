package com.example.springboot_01.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String image;
    private Long shopId;
    private Long categoryId;
    private LocalDateTime createTime;
    //private LocalDateTime updateTime;
}

