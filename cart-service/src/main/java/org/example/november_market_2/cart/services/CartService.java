package org.example.november_market_2.cart.services;

import lombok.RequiredArgsConstructor;
import org.example.november_market_2.api.ProductDto;
import org.example.november_market_2.cart.integrations.ProductServiceIntegration;
import org.example.november_market_2.cart.utils.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor            //  позволяет не инжектить корзину, это важно, потому что у нас нет такого бина
public class CartService {
    private final ProductServiceIntegration productServiceIntegration;

    private final RedisTemplate<String, Object> redisTemplate;

    public Cart getCurrentCart(String cartId) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cartId))) {
            // если корзины с таким id нет, ее надо создать и положить в Redis
            Cart cart = new Cart();
            redisTemplate.opsForValue().set(cartId, cart);
        }
        return (Cart)redisTemplate.opsForValue().get(cartId);
    }

    public void addToCart(String cartId, Long productId) {
        execute(cartId, cart -> {
            ProductDto p = productServiceIntegration.findById(productId);
            cart.add(p);
        });
    }

    public void clearCart(String cartId) {
        execute(cartId, Cart::clear);
    }

    public void decrease(String cartId, Long productId) {
        execute(cartId, cart -> {
            ProductDto p = productServiceIntegration.findById(productId);
            cart.decrease(p);
        });
    }

    public void mergeCarts(String username, String guestCartId) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(guestCartId))) {
            execute (guestCartId, cart -> {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(username))) {
                    Cart newCart = new Cart();
                    newCart.setTotalPrice(cart.getTotalPrice());
                    for (int i = 0; i < cart.getItems().size(); i++) {
                        for (int j = 0; j < cart.getItems().get(i).getQuantity(); j++) {
                            newCart.add(productServiceIntegration.findById(cart.getItems().get(i).getProductId()));
                        }
                    }
                    redisTemplate.opsForValue().set(username, newCart);
                    clearCart(guestCartId);
                } else {
                    getCurrentCart(username).setTotalPrice(getCurrentCart(username).getTotalPrice().add(cart.getTotalPrice()));
                    for (int i = 0; i < cart.getItems().size(); i++) {
                        for (int j = 0; j < cart.getItems().get(i).getQuantity(); j++) {
                            addToCart(username, cart.getItems().get(i).getProductId());
                        }
                    }
                    clearCart(guestCartId);
                }
            });
        }
    }

    private void execute(String cartId, Consumer<Cart> action) {
        Cart cart = getCurrentCart(cartId);
        action.accept(cart);
        redisTemplate.opsForValue().set(cartId, cart);
    }
}
