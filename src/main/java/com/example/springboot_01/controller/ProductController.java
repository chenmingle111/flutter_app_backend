package com.example.springboot_01.controller;
import com.example.springboot_01.common.Result;
import com.example.springboot_01.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public Result queryTypeList() {
        return categoryService.queryByList();
    }

    @GetMapping("list/type/{id}")
    public Result queryTypeDetail(@PathVariable Integer id) {
        log.debug("list/type/{id}");
        return categoryService.queryByType(id);
    }

    @GetMapping("introduct/{id}")
    public Result  queryDetail(@PathVariable Integer id) {
        log.debug("introduct/{id}");
        return categoryService.queryIntroById(id);
    }

}
