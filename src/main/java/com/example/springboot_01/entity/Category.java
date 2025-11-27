package com.example.springboot_01.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "category")
public class Category {
    private Long id;

    private String name;

    private String description;

    private Long parentId ;

}
