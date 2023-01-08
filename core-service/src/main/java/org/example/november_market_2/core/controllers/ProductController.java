package org.example.november_market_2.core.controllers;

import lombok.RequiredArgsConstructor;
import org.example.november_market_2.api.PageDto;
import org.example.november_market_2.api.ProductDto;
import org.example.november_market_2.core.converters.PageConverter;
import org.example.november_market_2.core.converters.ProductConverter;
import org.example.november_market_2.core.entities.Product;
import org.example.november_market_2.core.exceptions.ResourceNotFoundException;
import org.example.november_market_2.core.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductConverter productConverter;

    private final PageConverter pageConverter;


    @GetMapping
    public PageDto getAllProducts(
            @RequestParam(name = "p", defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "productFilter", required = false) String titlePart,
            @RequestParam(name = "priceMinFilter", required = false) String minPrice,
            @RequestParam(name = "priceMaxFilter", required = false) String maxPrice
    ) {
        if (page < 1) {
            page = 1;
        }
        Optional<String> productFilter = (titlePart!=null)?(Optional.of(titlePart)):Optional.empty();
        Optional<String> minPriceStr = (minPrice!=null)?(Optional.of(minPrice)):Optional.empty();
        Optional<String> maxPriceStr = (maxPrice!=null)?(Optional.of(maxPrice)):Optional.empty();

        Page<ProductDto> p = productService.findWithFilter(page-1, pageSize, productFilter, minPriceStr, maxPriceStr);

        return pageConverter.entityToDto(p);
    }



    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        Optional<Product> p = productService.findById(id);
        return productConverter.entityToDto(p.orElseThrow(() ->
                new ResourceNotFoundException("Продукт с id = " + id + " не найден")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewProduct(@RequestBody ProductDto productDto) {
        productService.createNewProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
    }
}

