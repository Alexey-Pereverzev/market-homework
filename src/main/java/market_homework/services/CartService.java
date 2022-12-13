package market_homework.services;


import market_homework.entities.Product;
import market_homework.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import market_homework.utils.Cart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor            //  позволяет не инжектить корзину, это важно, потому что у нас нет такого бина
public class CartService {
    private final ProductService productService;
    private Cart cart;

    @PostConstruct
    public void init() {
        cart = new Cart();
        cart.setItems(new ArrayList<>());
    }

    public Cart getCurrentCart() {
        return cart;
    }

    public void addToCart(Long productId) {
        Product p = productService.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Продукт с id: " + productId + " не найден"));
        cart.add(p);
    }

    public void clearCart() {
        cart.clear();
    }

    public void decrease(Long productId) {
        Product p = productService.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Продукт с id: " + productId + " не найден"));
        cart.decrease(p);
    }
}