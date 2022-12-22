package org.example.november_market_2.core.integrations;

import lombok.RequiredArgsConstructor;
import org.example.november_market_2.api.CartDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final RestTemplate restTemplate;

    public CartDto getCurrentCart() {
        return restTemplate.getForObject("http://localhost:8190/market-cart/api/v1/cart", CartDto.class);
    }
}