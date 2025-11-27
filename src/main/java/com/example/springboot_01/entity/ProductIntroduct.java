package com.example.springboot_01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_introduct")
public class ProductIntroduct {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String introduct;
}
