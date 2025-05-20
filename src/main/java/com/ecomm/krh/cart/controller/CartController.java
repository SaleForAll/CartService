package com.ecomm.krh.cart.controller;

import com.ecomm.krh.model.CartItem;
import com.ecomm.krh.services.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private static final Logger logger = LogManager.getLogger(CartController.class);
    @Autowired
    private CartService cartService;


    @PostMapping("/addCart/{userId}")
    public String addToCart(@PathVariable String userId, @RequestBody CartItem item) {
        logger.info("add to cart ");
        cartService.addToCart(userId, item);
        return "Item added to cart 888";
    }

    @GetMapping("/getCart/{userId}")
    public List<CartItem> getCart(@PathVariable String userId) {
        logger.info("get cart call ");
        return cartService.getCart(userId);
    }


    @DeleteMapping("/clearCart/{userId}")
    public String clearCart(@PathVariable String userId) {
        logger.info("clear cart call ");
        cartService.clearCart(userId);
        return "Cart cleared";
    }

    @GetMapping("/products/{id}")
    public Mono<String> getProduct(@PathVariable int id) {
        return cartService.getProduct(id);
    }
}

