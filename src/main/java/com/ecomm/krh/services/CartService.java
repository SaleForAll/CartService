package com.ecomm.krh.services;

import com.ecomm.krh.model.CartItem;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final String CART_PREFIX = "cart:";

    private final HashOperations<String, String, Integer> hashOps;
    private final WebClient webClient;
    @Autowired
    public CartService(RedisTemplate<String, String> redisTemplate,WebClient webClient) {
        this.hashOps = redisTemplate.opsForHash();
        this.webClient = webClient;
    }
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    public void addToCart(String userId, CartItem item) {
        String key = CART_PREFIX + userId;
        hashOps.increment(key, item.getProductId(), item.getQuantity());
    }

    public List<CartItem> getCart(String userId) {
        String key = CART_PREFIX + userId;
        Map<String, Integer> entries = hashOps.entries(key);
        return entries.entrySet().stream()
                .map(e -> new CartItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public void clearCart(String userId) {
        String key = CART_PREFIX + userId;
        hashOps.getOperations().delete(key);
    }

    public Mono<String> getProduct(int productId) {
        return webClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public String fallbackMethod(Throwable t) {
        return "Fallback response";
    }
}

