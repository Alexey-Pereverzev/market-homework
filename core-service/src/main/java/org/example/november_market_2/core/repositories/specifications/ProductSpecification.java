package org.example.november_market_2.core.repositories.specifications;

import org.example.november_market_2.core.entities.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> productLike(String title) {
        return ((root, query, cb) -> cb.like(root.get("title"), "%" + title + "%"));
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return ((root, query, cb) -> cb.ge(root.get("price"), minPrice));
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return ((root, query, cb) -> cb.le(root.get("price"), maxPrice));
    }
}
