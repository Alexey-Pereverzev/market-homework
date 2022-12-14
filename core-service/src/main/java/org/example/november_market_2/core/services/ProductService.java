package org.example.november_market_2.core.services;

import lombok.RequiredArgsConstructor;
import org.example.november_market_2.api.ProductDto;
import org.example.november_market_2.core.converters.ProductConverter;
import org.example.november_market_2.core.entities.Product;
import org.example.november_market_2.core.exceptions.ResourceNotFoundException;
import org.example.november_market_2.core.repositories.ProductMatrix;
import org.example.november_market_2.core.soap.products.ProductSoap;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMatrix matrix;
    private final CategoryService categoryService;

    private final ProductConverter productConverter;

    public List<Product> findAll() {
        return matrix.findAll();
    }

    public void deleteById(Long id) {
        matrix.deleteById(id);
    }

    public void createNewProduct(ProductDto productDto) {
        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setPrice(productDto.getPrice());
        product.setCategory(categoryService.findByTitle(productDto.getCategoryTitle())
                .orElseThrow(() -> new ResourceNotFoundException("Категория с названием: "
                        + productDto.getCategoryTitle() + " не найдена")));
        matrix.save(product);
    }

    public Optional<Product> findById(Long id) {
        return matrix.findById(id);
    }

    public static final Function<Product, ProductSoap> functionEntityToSoap = pe -> {
        ProductSoap p = new ProductSoap();
        p.setId(pe.getId());
        p.setTitle(pe.getTitle());
        p.setPrice(pe.getPrice());
        p.setCategoryTitle(pe.getCategory().getTitle());
        return p;
    };

    public ProductSoap getByTitle(String title) {
        return matrix.findByTitle(title).map(functionEntityToSoap).get();
    }

    public List<ProductSoap> getAllProducts() {
        return matrix.findAll().stream().map(functionEntityToSoap).collect(Collectors.toList());
    }

    public static Optional<BigDecimal> strToDecimal(Optional<String> str) {
        if (str.isEmpty()) {
            return Optional.empty();
        }
        try {
            Double.parseDouble(str.get());
            return Optional.of(new BigDecimal(str.get()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public List<ProductDto> findWithFilter(Optional<String> productFilter,
                                           Optional<String> minPriceStr,
                                           Optional<String> maxPriceStr) {

        Optional<BigDecimal> minPrice = strToDecimal(minPriceStr);
        Optional<BigDecimal> maxPrice = strToDecimal(maxPriceStr);

        Specification<Product> spec = Specification.where(null);

        if (productFilter.isPresent() && !productFilter.get().isBlank()) {
            spec = spec.and(ProductSpecification.productLike(productFilter.get()));
        }
        if (minPrice.isPresent()) {
            spec = spec.and(ProductSpecification.minPrice(minPrice.get()));
        }
        if (maxPrice.isPresent()) {
            spec = spec.and(ProductSpecification.maxPrice(maxPrice.get()));
        }
        return matrix.findAll(spec).stream().map(productConverter::entityToDto).collect(Collectors.toList());
    }
}

