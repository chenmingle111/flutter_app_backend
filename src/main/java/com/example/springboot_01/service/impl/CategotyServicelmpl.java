package com.example.springboot_01.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_01.common.Result;
import com.example.springboot_01.entity.Category;
import com.example.springboot_01.entity.Product;
import com.example.springboot_01.entity.ProductIntroduct;
import com.example.springboot_01.mapper.CategoryMapper;
import com.example.springboot_01.mapper.ProductIntroductMapper;
import com.example.springboot_01.service.CategoryService;
import com.example.springboot_01.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.springboot_01.utils.RedisConstants.MINI_CATEGOTY_KEY;

@Service
public class CategotyServicelmpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ProductMapper productMapper;

    public CategotyServicelmpl(StringRedisTemplate stringRedisTemplate, ProductMapper productMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public Result queryByList() {
        log.debug("---------类别------------------");
        Set<String> shopTypeJsonSet = stringRedisTemplate.opsForZSet().range(MINI_CATEGOTY_KEY, 0, -1);
        if (shopTypeJsonSet != null && !shopTypeJsonSet.isEmpty()) {
            List<Category> shopTypeList = shopTypeJsonSet.stream()
                    .map(json -> JSONUtil.toBean(json, Category.class))
                    .collect(Collectors.toList());

            return Result.ok(shopTypeList);
        }
        List<Category> typeList = query().orderByAsc("id").list();
        if (typeList == null || typeList.isEmpty()) {
            return Result.fail("分类查找失败");
        }
        for (Category type : typeList) {
            stringRedisTemplate.opsForZSet().add(MINI_CATEGOTY_KEY, JSONUtil.toJsonStr(type), type.getId());
        }
        return Result.ok(typeList);
    }

    @Override
    public Result<List<Product>> queryByType(Integer categoryId) {
        log.debug("---------类别商品------------------");
        if (categoryId == null) {
            return Result.fail("Category ID cannot be null");
        }
        // 使用 MyBatis-Plus LambdaQueryWrapper 过滤 categoryId
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId);

        List<Product> products = productMapper.selectList(wrapper);

        return Result.ok(products);
    }

    @Autowired
    private ProductIntroductMapper productIntroductMapper;

    @Override
    public Result queryIntroById(Integer id) {
        ProductIntroduct productIntroduct = productIntroductMapper.selectById(id);
        if (productIntroduct != null) {
            return Result.ok(productIntroduct.getIntroduct());
        }
        return Result.fail("Introduction not found");
    }
}
