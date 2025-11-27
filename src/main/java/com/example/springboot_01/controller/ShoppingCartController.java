package com.example.springboot_01.controller;

import com.example.springboot_01.common.Result;
import com.example.springboot_01.dto.CartItemDTO;
import com.example.springboot_01.service.IShoppingCartService;
import com.example.springboot_01.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result addToCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        shoppingCartService.addToCart(userId, cartItemDTO);
        return Result.ok();
    }

    @PostMapping("/update")
    public Result updateCartItem(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        shoppingCartService.updateCartItem(userId, cartItemDTO);
        return Result.ok();
    }

    @PostMapping("/remove")
    public Result removeCartItem(HttpServletRequest request, @RequestParam Long productId) {
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        shoppingCartService.removeCartItem(userId, productId);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<List<CartItemDTO>> getCartItems(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        List<CartItemDTO> cartItems = shoppingCartService.getCartItems(userId);
        return Result.ok(cartItems);
    }

    @PostMapping("/sync")
    public Result syncCart(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        shoppingCartService.syncCart(userId);
        return Result.ok();
    }

    @PostMapping("/create")
    public Result createCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {
        log.debug("-------------------加入购物车---------------------");
        Long userId = getUserIdFromToken(request);
        if (userId == null)
            return Result.fail("Invalid token");

        shoppingCartService.addShoppingCart(userId, cartItemDTO);
        return Result.ok();
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String token = auth.substring(7);
        return JwtUtils.parseToken(token);
    }
}
